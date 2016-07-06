class User < ActiveRecord::Base
  include Uuidable
  # Include default devise modules. Others available are:
  # :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable, :omniauthable,
    :recoverable, :rememberable, :trackable, :validatable #, :omniauth_providers => [:facebook]
  has_many :projects
  has_many :reports
end
