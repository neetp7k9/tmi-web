require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class Images < Grape::API
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
       
      resource :image do  
        desc 'use to upload image to solr and save index in outfile.xml'
        params do
          requires :image_id, type: Integer,  desc: 'image id'
        end
     
        desc 'Image information'
        get do
          Image.find(params[:image_id])
        end
     
        params do
          requires :document_id, type: Integer,  desc: 'document id'
          requires :file, type: File,  desc: 'image'
        end
        desc 'create a new image'
        post do
          p "create image"

          image_params = {}
          image_params[:document_id] = params[:document_id]

          new_file = ActionDispatch::Http::UploadedFile.new(params[:file])
          image_params[:avatar] = new_file
          image = Image.new(image_params)
          image.user_id = current_user.id 
          image.save
          image_id = image.id
          p "save image"
          start = Time.now.to_f
          image_path = URI.unescape(image.avatar.url(:original).split("?")[0])
          p image_path
          file_path = "#{root_path}/#{image_id}"
          File.open(file_path, "wb") { |f| f.write(Rails.root.to_s + "/public" + image_path) }
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          response = solr.get '/index', :params => {:wt=>"xml", :user_id => current_user.id, :file => file_path}
          p "finish indexing image"
          return image_id
        end
      end
      params do
        requires :image_id, type:Integer, desc: 'image id'
      end
      desc "delete image"
      delete do
        image = Image.find(image_id)
        image.destroy
        return [image]
      end      
    end
  end
end
