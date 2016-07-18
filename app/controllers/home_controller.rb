class HomeController < ApplicationController
  before_action :authenticate_user!
  def home
    @user = current_user
    p @user
    @projects = Project.where user_id: @user.id
    @projects ||= [] 
  end

  def upload
    
  end

  def myClothes
    @user = current_user
    @mySearches = Search.where(user_id: @user.id)
  end

  def logout
    sign_out current_user
    redirect_to :root
  end
end
