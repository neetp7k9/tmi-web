module API
  module V1
    class Projects < Grape::API
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
      resource :project do
        desc 'use to create project folder or display document in a folder'
        params do
          requires :name, type: String, desc: 'display name'
        end
        post do
          project = Project.new
          project.name = params[:name]
          project.user_id = current_user.id 
	  project.save
          project_id = project.id
          return project_id
        end
        
        desc 'use to get a list of document ids'
        params do
          requires :project_id, type: Integer, desc: 'project id'
        end
        get do # return document ID list
           response = {}
           project = Project.find(params[:project_id])
           response[:project] = project
           response[:DocumentList] = project.documents
           return response 
        end

        desc 'delete project'
        params do
          requires :project_id, type: Integer, desc: 'project id'
        end
        delete do # return document ID list
           p "get call by #{params[:project_id]}"
           response = {}
           project = Project.find(params[:project_id])
           project.destroy
           response[:project] = project
           response[:DocumentList] = project.documents
           return "" 
        end
      end
    end
  end
end
