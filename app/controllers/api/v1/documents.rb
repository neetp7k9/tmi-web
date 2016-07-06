require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class Documents < Grape::API
      include Grape::Kaminari
      format :json
      formatter :json, Grape::Formatter::ActiveModelSerializers
      root_path = Rails.root.to_s + "/public/tmp/"

      before do
        error!("401 Unauthorized", 401) unless authenticated
      end

      helpers do
        def warden
          env['warden']
        end

        def authenticated
           return true if warden.authenticated?
           params[:access_token] && @user = User.find_by(uuid: params[:access_token])
        end

        def current_user
          warden.user || @user
        end
      end
      resource :document do
        
        params do
          requires :project_id, type:Integer, desc: 'project id'
          requires :file, type: File, desc: 'document'
        end
        desc 'Create a new Document'
        post do #add document
          start = Time.now.to_f
          document_params = {}
	  document_params[:project_id] = params[:project_id]
	  document_params[:name] = params[:file].filename
          p "create document : " +  params[:file].filename
          new_file = ActionDispatch::Http::UploadedFile.new(params[:file])
          document_params[:avatar] = new_file
          document = Document.new(document_params)
          document.user_id = current_user.id 
          document.save
          p Time.now.to_f - start
          document_id = document.id
           
          document.makePages
          
          if document.project_id == 0
            p "create document for search"
            search = Search.new
            search.document_id = document_id
            search.user_id = current_user.id 
            search.save
            search_id = search.id
            return [search_id, document_id]
          else 
            p "create document image"
            image_params = {}
            image_params[:document_id] = document_id
            image_params[:project_id] = params[:project_id]
            image_params[:avatar] = document.avatar
            image = Image.new(image_params)
            image.user_id = current_user.id 
            image.save
            image_id = image.id
            image_path = URI.unescape(image.avatar.url(:origin).split("?")[0])
            file_path = "#{root_path}/#{image_id}"
            File.open(file_path, "wb") { |f| f.write(Rails.root.to_s + "/public" + image_path) }
            p "indexing document image"
            solr = RSolr.connect :url => 'http://127.0.0.1:8000'
            response = solr.get '/index', :params => {:wt=>"xml", :user_id => current_user.id, :file => file_path}

	    file_path = Rails.root.to_s + "/public" + URI.unescape(document.avatar.url.split("?")[0])
            response = solr.get '/index_document', :params => {:wt => "xml", :user_id => current_user.id, :document_id => document_id, :file => file_path }
            p "finish creating document"

            return document_id      
          end
        end
        
        params do
            requires :document_id, type:Integer, desc: 'document id'
        end
        desc "get list images id and path"
        get do # get a list of image ids"
          document = Document.find(document_id) 
          return [document, document.images]
        end

        params do
            requires :document_id, type:Integer, desc: 'document id'
        end
        desc "delete document"
        delete do 
          document = Document.find(params[:document_id]) 
          document.destroy
          return [document, document.images]
        end
      
      end
    end
  end
end       
