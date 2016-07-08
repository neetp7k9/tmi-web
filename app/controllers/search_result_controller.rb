class SearchResultController < ApplicationController
  before_action :authenticate_user!
  def show 
    @user = current_user
    @search_result = SearchResult.find(params[:id])
    @info = @search_result.response
    @search_target  = @search_result.search_target
    if @search_result.search_type == 0
       @nextPageTitle = "local feature result"
       @nextPageURL = "/search_results/"+(@search_result.id+1).to_s
    end
     
    if @search_result.search_type == 1
       @nextPageTitle = "global feature result"
       @nextPageURL = "/search_results/"+(@search_result.id-1).to_s
    end
    
  end
end
