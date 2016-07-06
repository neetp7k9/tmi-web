source 'https://rubygems.org'

# Bundle edge Rails instead: gem 'rails', github: 'rails/rails'
gem 'rails', '4.2.5.2'
gem 'rails-i18n', '~> 4.0.0' # For 4.0.x
gem 'rails_12factor', group: :production

#application
gem "rsolr"
gem "pdf-reader"
gem "html2slim"

# database
gem 'mysql2', '~> 0.3.20'
gem 'activerecord'
gem 'activesupport'
gem 'actionmailer'
gem 'actionpack'
gem 'textacular', '~> 3.0'
gem 'atomic_arrays'
gem 'bcrypt', '~> 3.1.7'

# geolocation
gem 'rgeo', '0.4.0'
gem 'rgeo-activerecord'
gem 'activerecord-mysql2spatial-adapter', '~> 0.5.0.nonrelease'
gem 'rgeo-shapefile'
gem 'google_places'
gem 'foursquare2'

# web / templating
gem 'quiet_assets'
gem 'ransack'

gem 'bootstrap-sass', '~> 3.3.1'
gem 'jquery-rails', '~> 4.0.4'
gem 'sass-rails', '~> 5.0'
gem 'slim-rails'
 gem 'execjs' 
 gem 'therubyracer' 
 gem 'less-rails', '~> 2.7.1'

gem 'uglifier', '>= 2.7.2'
gem 'twitter-bootstrap-rails', '~> 3.2.0'
gem 'turbolinks'

gem 'kramdown'
gem 'themes_on_rails'

# server
gem 'puma'

# model
gem 'hashie-forbidden_attributes'

# pagination
gem 'kaminari'
gem 'will_paginate', require: false
gem 'api-pagination', require: false

# authentication
gem 'devise'
gem 'devise-bootstrap-views'

gem 'cancancan', '~> 1.10'
gem 'doorkeeper', '~> 2.2.2'
gem 'wine_bouncer', '~> 0.5.1'

gem 'omniauth'
gem 'omniauth-twitter'
gem 'omniauth-facebook'
gem 'omniauth-google-oauth2'
gem 'omniauth-bike-index'

gem 'armor'

# api
gem 'grape', '~> 0.14.0'
gem 'grape-active_model_serializers', git: 'https://github.com/jrhe/grape-active_model_serializers'
gem 'grape-entity', github: 'intridea/grape-entity', ref: '48e5be7df9e362edc452332375e9397b12abdd45'
gem 'grape-cache_control', '~> 1.0.1'
gem 'grape-swagger', github: 'bitwise-panda/grape-swagger', branch: 'swagger-2.0'
# gem 'grape-swagger', path: "~/Documents/Projects/ruby/grape-swagger", branch: "swagger-2.0"
gem 'grape-swagger-rails', github: 'bitwise-panda/grape-swagger-rails'
#gem 'grape-swagger-rails', path: "~/Documents/Projects/ruby/grape-swagger-rails"
gem 'grape_logging', '~> 1.1.2'
gem 'grape-kaminari'
gem 'grape-route-helpers'

gem 'swagger-ui_rails', github: '3scale/swagger-ui_rails'
gem 'rack-cors', require: 'rack/cors'
gem 'active_model_serializers'

# deployment and configuration
gem 'dotenv'
gem 'dotenv-deployment'
gem 'dotenv-rails'
gem 'settingslogic'
gem 'figaro'

# file upload
gem 'paperclip'
gem 'mini_magick'

# json
gem 'oj'
gem 'oj_mimic_json'

# performance and caching
gem 'garner'
gem 'redis-activesupport'

# programming enchance
gem 'parallel', require: false
gem 'redlock'

# services
gem 'sinatra', '>= 1.3.0', require: nil
gem 'sentry-raven', require: false
gem 'geocoder'
gem 'pusher'

# monitoring
# gem 'appsignal'
# gem 'grape-appsignal', github: 'madglory/grape-appsignal'

# State machine
gem 'aasm'
gem 'state_machine'

# HTTP client
gem 'httparty'

# Memcached client
gem 'dalli'
gem 'memcache-client', require: 'memcache'

# CMS
gem 'storytime', github: 'CultivateLabs/storytime', branch: 'dev'

# CLI
gem 'cocaine'
gem 'ruby-progressbar'

# Concurrency
gem 'celluloid'

# Error Handling
gem 'better_errors'

# Deployment tool
# gem 'capistrano'
# gem 'whiskey_disk'

# Debugger (ruby-debug for Ruby 1.8.7+, ruby-debug19 for Ruby 1.9.2+)
# gem 'ruby-debug'
# gem 'ruby-debug19'

# Background Processing
# gem 'delayed_job'
# gem 'resque'

# Full-text search engine
# gem 'thinking-sphinx', '~> 2.0.2', :require => 'thinking_sphinx'

group :development, :test, :production do
  gem 'factory_girl', '~> 4.3.0', require: false
  gem 'ffaker', require: false
  gem 'cpf_faker', require: false
end

group :development, :test do
  gem 'guard'
  gem 'guard-puma'
  gem 'guard-rspec', '4.2.8'
  gem 'guard-livereload'
  gem 'rspec', '~> 3.2.0'
  gem 'awesome_print'
  gem 'pry'
  gem 'pry-byebug', '1.3.3'
  gem 'pry-remote'
  gem 'foreman'
  gem 'rerun'
  gem 'vcr'
  gem 'rspec-rails'
  gem 'factory_girl_rails'
  gem 'shoulda-matchers'
  gem 'growl'
  gem 'minitest'
  gem 'database_cleaner'
  gem 'database_rewinder', github: 'ntxcode/database_rewinder', branch: 'filtering_interface'
  gem 'knapsack'
  gem 'rubocop'
  gem 'rspec-nc', github: 'estevaoam/rspec-nc'
  gem 'byebug'
  gem 'web-console', '~> 2.0'
  #   gem 'spring', '~> 1.3.6'
end

group :profile do
  gem 'stackprof'
end

