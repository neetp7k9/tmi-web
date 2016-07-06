require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class Searches < Grape::API
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
       
      resource :search_by_image do
        desc 'search by image'
        params do
          requires :file, type: File,  desc: 'image'
          optional :crop_info, type: String, default: "", desc: 'crop position info'
          optional :document_id, type: Integer, default: 0, desc: 'document id'
          optional :search_id, type: Integer, default: 0, desc: 'search id'
        end
        post do
          p "save image for searching"
          image_params = {}
          image_params[:document_id] = params[:document_id]
          crop_data = params[:crop_info].split
          crop_info = "#{crop_data[0].to_i} #{crop_data[1].to_i} #{crop_data[0].to_i+crop_data[2].to_i} #{crop_data[1].to_i+crop_data[3].to_i}"
   
          image_params[:crop_info] = crop_info
          image_params[:project_id] = 0 
          new_file = ActionDispatch::Http::UploadedFile.new(params[:file])
          image_params[:avatar] = new_file
          image = Image.new(image_params)
          image.user_id = current_user.id 
          image.save
          image_id = image.id
          p "start searching"
          image_path =  Rails.root.to_s + "/public" + URI.unescape(image.avatar.url(:original).split("?")[0])
          p image_path 
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          search_id = params[:search_id]
          unless params[:search_id]
            search = Search.new
            search.document_id = params[:document_id]
            search.user_id = current_user.id 
            search.save
            search_id = search.id
          end

          p "do global Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = search_id
          response = solr.get '/search', :params => {:wt => "xml", :user_id => current_user.id, :file => image_path}
          searchResult.result = response.to_s
          searchResult.search_type = 0
          searchResult.save
          p "finish global Search"

          p "do local Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = params[:search_id]
          response = solr.get '/search', :params => {:wt => "xml", :user_id => current_user.id, :file => image_path, :feature => "local feature"}
          searchResult.result = response.to_s
          searchResult.search_type = 1
          searchResult.save
          p "finish local Search"

          p "finish search"

          return "success"
        end
      end
    resource :search_by_text do
      desc 'search by document'
      params do
        requires :text, type: String,  desc: 'text to search'
      end
      post do
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          p params[:text]
          search = Search.new
          search.user_id = current_user.id 
          search.save
          search_id = search.id

          searchResult = SearchResult.new
          searchResult.search_id = search_id
          response = solr.get '/search_text', :params => {:wt => "xml", :user_id => current_user.id, :text => params[:text] }
          searchResult.result = response.to_s
          p "finish search"
          searchResult.search_text = params[:text]
          searchResult.search_type = 2
          searchResult.save
          p "finish save"

          return search_id

      end
    end
      resource :search_by_document do
        desc 'search by document'
        params do
          requires :document_id, type:Integer, desc: 'document id'
          requires :search_id, type: Integer, desc: 'search id'
        end
        get do
          p "start search by document"
          
          p params[:document_id]

          document_id = params[:document_id]
          document = Document.find(document_id)
          image_params = {}
          image_params[:document_id] = document_id
          image_params[:project_id] = 0
          image_params[:avatar] = document.avatar
          image = Image.new(image_params)
          image.user_id = current_user.id 
          image.save
          image_id = image.id
          p "save document image"
          image_path =  Rails.root.to_s + "/public" + URI.unescape(image.avatar.url(:origin).split("?")[0])
          p image_path 
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          search_id = params[:search_id]

          p "do global Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = search_id
          response = solr.get '/search', :params => {:wt => "xml", :user_id => current_user.id,  :file => image_path}
          p response
          searchResult.result = response.to_s
          searchResult.search_type = 0
          searchResult.save
          p "finish global Search"

          p "do local Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = params[:search_id]
          response = solr.get '/search', :params => {:wt => "xml", :user_id => current_user.id, :file => image_path, :feature => "local feature"}
          p response
          searchResult.result = response.to_s
          searchResult.search_type = 1
          searchResult.save
          p "finish local Search"

          p "finish search"

          return "success"
        end
      end
      resource :import_file do
        desc 'import many document'
        params do
          requires :project_id, type:Integer, desc: 'project id'
          requires :name, type: String, default: '', desc: 'name'
          requires :dir, type: String, default: '', desc: 'directory'
        end
        get do
          p params[:project_id]
          p params[:dir]
          p params[:name]
	  
	   
	end	

	params do
          requires :file, type: File, desc: 'compact file'
        end
        post do
          p "unpack the file"
          p params[:file]
	  # problem 1 how to manage the tmp file and where to put the unzip file

	  file_path = "#{root_path}/#{params[:file].filename}"
          FileUtils.mv params[:file].tempfile, file_path, {:force=>true}
          p "unzip "+ file_path 
          system "unzip -o "+ file_path + " -d " + root_path 
          project = Project.new
	  project.name = params[:file].filename[0..-5]
          project.save
          project_id = project.id
          p "create new project #{project.id}"
	  dir = file_path[0..-5]
	  entries = Dir.entries(dir)
	  entries.delete(".")
	  entries.delete("..")
	  { "project_id"=>project_id,"dir"=>dir, "file" => entries}
	end
      end
    end
  end
end
