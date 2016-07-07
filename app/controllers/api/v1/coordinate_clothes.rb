require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class CoordinateClothes < Grape::API
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
      resource :coordinate_cloth do
        params do
          requires :clothes, type: File, desc: 'cloth is required'
          requires :pants, type: File, desc: 'pant is required'
          optional :shoes, type: File, desc: 'shoes is optional'
          optional :hat, type: File, desc: 'hat is optional' 
          optional :others, type: File, desc: 'others is optional'
        end
        desc 'Create a new coordinate_cloth'
        post do 
          document = CoordinateClothe.new
          document.save
          p "tmi"
          document_id = document.id
          [:clothes, :pants, :shoes, :hat, :others].each do |key| 
            next unless params[key]
            image_params = {}
            image_params[:coordinate_clothe_id] = document_id
            image_params[:clothes_type] = @@type_hash[key] 
            p @@type_hash[key]
            new_file = ActionDispatch::Http::UploadedFile.new(params[key])
            image_params[:avatar] = new_file
            image = Image.new(image_params)
            image.save
            p "create document image"
            next if key == :clothes 
            image_path = URI.unescape(image.avatar.url(:origin).split("?")[0])
            p "create document image"
            file_path = "#{root_path}/#{image.id}"
            File.open(file_path, "wb") { |f| f.write(Rails.root.to_s + "/public" + image_path) }
            p "indexing document image"
            solr = RSolr.connect :url => 'http://127.0.0.1:8000'
            response = solr.get '/index', :params => {:wt=>"xml", :type => @@type_hash[key], :file => file_path}

            p "finish creating document"
          end
            return document_id      
        end
        
      end
    end
  end
end       
