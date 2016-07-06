require 'grape'

module API
  module V1
    module Entities    
      class User < Grape::Entity
        expose :uuid, documentation: { type: "Integer", desc: "uuid"}
        expose :name, documentation: { type: "String", desc: "name" }
        expose :email, documentation: { type: "String", desc: "email" }
      end            
    end
  end
end