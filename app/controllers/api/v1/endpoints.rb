module API
    module V1
        extend ActiveSupport::Concern
        class Endpoints < Grape::API
            version 'v1', using: :path, vendor: 'sudo', cascade: false

            mount API::V1::Users
            mount API::V1::Images
            mount API::V1::Projects
            mount API::V1::Documents
            mount API::V1::Searches
            mount API::V1::Reports
            mount API::V1::Scripts
        end
    end
end

