module API
  module V1
    class Users < Grape::API
      include Grape::Kaminari
      format :json
      formatter :json, Grape::Formatter::ActiveModelSerializers
      root_path = Rails.root.to_s + "/searchServer/index/"
      resource :users do
        # POST /v1/users
        desc 'add user' do
          detail 'this will expose all the kittens'
        end
        params do
          requires :email, type: String, desc: 'email'
          requires :password, type: String, desc: 'password'
        end
        post '/', http_codes: [
          [200, 'Ok', API::V1::Entities::User],
          [400, 'Invalid parameter entry']
        ] do
          user = User.new
          user.email = params[:email]
          user.password = params[:password]
          user.save!
          #create user folder
          #local global text
          FileUtils.mkdir root_path + "global/" + user.id.to_s 
          FileUtils.mkdir root_path + "local/" + user.id.to_s
          FileUtils.mkdir root_path + "text/" + user.id.to_s 
          FileUtils.cp_r    root_path + "global.config",  root_path + "global/"  
          FileUtils.mv    root_path + "global/global.config",  root_path + "global/" + user.id.to_s + ".config/" 
          FileUtils.cp_r    root_path + "local.config",  root_path + "local/"  
          FileUtils.mv    root_path + "local/local.config",  root_path + "local/" + user.id.to_s + ".config/" 
          
          {
            user: user
          }
        end

        # GET /v1/users/
        desc 'get users',
          notes: <<-NOTE
        Marked down notes!
        NOTE
        oauth2 'public', 'write'
        params do
          optional :query, type: String, desc: 'find user'
        end
        paginate per_page: 10, max_per_page: 100, offset: 0
        get '/', http_codes: [
          [200, 'Ok', API::V1::Entities::User],
          [400, 'Invalid parameter entry']
        ] do
          query = params[:query].presence || ''
          results = User.where(["email LIKE ?", "%#{params[:query]}%"]).order(:email)

          users = paginate(results)
          render users
        end

        # GET /v1/users/{uuid}
        desc 'get user by uuid',
          hidden: false,
          is_array: true,
          nickname: 'getUsers',
        http_codes: [
          [200, 'OK'],
          [401, 'KittenBitesError']
        ] # use http_codes instead of failure

        params do
          requires :uuid, type: String, desc: 'user uuid'
          requires :action, type: Symbol, values: [:PAUSE, :RESUME, :STOP], documentation: { param_type: 'query' }
          requires :id, type: Integer, desc: 'Coffee ID'
          requires :temperature, type: Integer, desc: 'Temperature of the coffee in celcius', documentation: { example: 72 }
        end
        get '/:uuid/' do
          user = User.where(uuid: params[:uuid]).first || fail(ActiveRecord::RecordNotFound)

          {
            user: user
          }
        end

        # DELETE /v1/users/
        desc 'delete user'
        params do
          requires :uuid, type: String, desc: 'user uuid'
        end
        delete '/:uuid/' do
          user = User.where(uuid: params[:uuid]).first || fail(ActiveRecord::RecordNotFound)
          user.destroy!

          {
            user: user
          }
        end

        # PUT /v1/users/
        desc 'update user'
        params do
          requires :uuid, type: String, desc: 'user uuid'
          optional :password, type: String, desc: 'password'
        end
        put '/:uuid/' do
          user = User.where(uuid: params[:uuid]).first || fail(ActiveRecord::RecordNotFound)
          user.password = param[:password]
          user.save!

          {
            application: application
          }
        end
      end
    end
  end
end
