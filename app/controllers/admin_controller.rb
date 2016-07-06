class AdminController < ApplicationController
  before_action :authenticate_user!  
  def reports
    @reports = Report.all
  end
end
