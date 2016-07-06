require 'grape-swagger'
require 'grape-active_model_serializers'
require 'oj'
require 'will_paginate/array'
require 'action_controller/metal/strong_parameters'
require 'doorkeeper/grape/helpers'

module JSONErrorFormatter
  def self.call(message, backtrace, _options, _env)
    { response_type: 'error', response: message, backtrace: backtrace }.to_json
  end
end

module API
  class Endpoints < Grape::API
    helpers Doorkeeper::Grape::Helpers

    version 'v1', using: :header, vendor: 'sudo'
    default_format :json
    format :json
    formatter :json, Grape::Formatter::ActiveModelSerializers
    error_formatter :json, JSONErrorFormatter

    # authentication
    before do
      # doorkeeper_authorize!
    end

    # CORS
    before do
      header['Access-Control-Allow-Origin'] = '*'
      header['Access-Control-Request-Method'] = '*'
    end

    # filters
    rescue_from :all do |e|
      eclass = e.class.to_s
      message = "OAuth error: #{e}" if eclass.match('WineBouncer::Errors')
      status = case
               when eclass.match('OAuthUnauthorizedError')
                 401
               when eclass.match('OAuthForbiddenError')
                 403
               when eclass.match('RecordNotFound'), e.message.match(/unable to find/i).present?
                 404
               else
                 (e.respond_to? :status) && e.status || 500
      end
      opts = { error: "#{message || e.message}" }
      opts[:trace] = e.backtrace[0, 10] unless Rails.env.production?
      Rack::Response.new(opts.to_json, status,                                  'Content-Type' => 'application/json',
                                                                                'Access-Control-Allow-Origin' => '*',
                                                                                'Access-Control-Request-Method' => '*').finish
    end

    # mount endpoints
    mount API::V1::Endpoints

    # configure swagger endpoint
    add_swagger_documentation(
                              info: {
                                title: 'Nexus API',
                                description: 'Nexus System'
                              },
                              base_path: '/api',
                              api_version: 'v1',
                              mount_path: 'docs.json',
                              hide_format: true,
                              hide_documentation_path: true)


    route :any, '*path' do
      fail StandardError, 'Unable to find endpoint'
    end
  end
end
