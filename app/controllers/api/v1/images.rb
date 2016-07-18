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
          requires :file, type: File,  desc: 'image'
        end
        desc 'create a new image'
        post do
          p "create image"

          image_params = {}
          c = CoordinateClothe.new
          c.save
          image_params[:coordinate_clothe_id] = c.id 
          image_params[:clothes_type]
          new_file = ActionDispatch::Http::UploadedFile.new(params[:file])
          image_params[:avatar] = new_file
          image = Image.new(image_params)
          image.save
          p "image have saved"
          image_path = URI.unescape(image.avatar.url(:origin).split("?")[0])
          file_path = "#{root_path}/#{image.id}"
          File.open(file_path, "wb") { |f| f.write(Rails.root.to_s + "/public" + image_path) }
          p "file_path =>  #{file_path}"
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          response = solr.get '/index', :params => {:wt=>"xml", :type => 0, :file => file_path}

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
