require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class Searches < Grape::API
      @@type_hash = {:clothes=>0, :pants=>1, :shoes=>2, :hat=>3, :others=>4}
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
          optional :type, type: Integer, default: 0, desc: 'clothes type'
          optional :search_id, type: Integer, default: 0, desc: 'search id'
        end
        post do
          p "save image for searching"
          image_params = {}
          new_file = ActionDispatch::Http::UploadedFile.new(params[:file])
          image_params[:avatar] = new_file
          image = Image.new(image_params)
          image.user_id = current_user.id 
          image.clothes_type = params[:type] 
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
          clothes_type = params[:type]
          unless params[:type]
            clothes_type = @@type_hash[:clothes]
          end


          p "do global Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = search_id
          response = solr.get '/search', :params => {:wt => "xml", :type => clothes_type, :file => image_path}
          searchResult.result = response.to_s
          searchResult.search_type = 0
          searchResult.save
          p "finish global Search"

          p "do local Search"
          searchResult = SearchResult.new
          searchResult.image_id = image_id
          searchResult.search_id = params[:search_id]
          response = solr.get '/search', :params => {:wt => "xml", :type => clothes_type, :file => image_path, :feature => "local feature"}
          searchResult.result = response.to_s
          searchResult.search_type = 1
          searchResult.save
          p "finish local Search"

          p "finish search"

          return "success"
        end
      end
    end
  end
end
