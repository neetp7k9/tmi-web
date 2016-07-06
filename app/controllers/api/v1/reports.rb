module API
  module V1
    class Reports < Grape::API
      include Grape::Kaminari
      format :json
      formatter :json, Grape::Formatter::ActiveModelSerializers

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
      resource :report do
        desc 'use to report a bug'
        params do
          requires :description, type: String, desc: 'description'
          requires :page_now, type: String, desc: 'page now'
        end
        post do
          report = Report.new
          report.description = params[:description]
          report.page_now = params[:page_now]
          report.user_id = current_user.id 
	  report.save
          return report.id 
        end
        
        desc 'use to get a report'
        params do
          requires :report_id, type: Integer, desc: 'report id'
        end
        get do # return document ID list
           response = {}
           report = Report.find(params[:report_id])
           response = report
           return response 
        end
      end
    end
  end
end
