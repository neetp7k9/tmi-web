class HomeController < ApplicationController
  before_action :authenticate_user!
  def home
    @user = current_user
    p @user
    @projects = Project.where user_id: @user.id
    @projects ||= [] 
  end

  def search_by_text
  end

  def index
  end

  def search_by_document
  end

  def crop_to_search
    @document_id = params[:id]
    @document = Document.find(@document_id)
    @search_id = params[:search_id]
  end
  def crop
    @id = params[:id]
    @document = Document.find(@id)
  end

  def search_result
  end
  def logout
    sign_out current_user
    redirect_to :root
  end
end
