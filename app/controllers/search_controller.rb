class SearchController < ApplicationController
  before_action :authenticate_user!
  def show

    @user = current_user
    @search_target = Search.find(params[:id]).search_target
    p @search_target
    @response = Search.find(params[:id]).response
  end
end
