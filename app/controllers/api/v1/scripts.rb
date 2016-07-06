require "rsolr"
require "pdf/reader"
require "uri"
module API
  module V1
    class Scripts < Grape::API
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
       
      resource :update_image_id do
        desc 'update image id'
        params do
          optional :user_id, type: Integer, default: 1, desc: 'user id'
          optional :feature, type: String, desc: 'feature'
        end
        get do
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          if params[:feature]
            response = solr.get '/update_image_id', :params => {:wt => "xml", :user_id => params[:user_id], :feature => "test"}
          else
            response = solr.get '/update_image_id', :params => {:wt => "xml", :user_id => params[:user_id]}
          end
          return response 
        end
      end
      resource :check_all do
        desc 'check all record'
        params do
          optional :user_id, type: Integer, default: 1, desc: 'user id'
          optional :feature, type: String, desc: 'feature'
        end
        get do
          solr = RSolr.connect :url => 'http://127.0.0.1:8000'
          if params[:feature]
            response = solr.get '/check_all', :params => {:wt => "xml", :user_id => params[:user_id], :feature => "test"}
          else
            response = solr.get '/check_all', :params => {:wt => "xml", :user_id => params[:user_id]}
          end
          return response 
        end
      end
    end
  end
end
