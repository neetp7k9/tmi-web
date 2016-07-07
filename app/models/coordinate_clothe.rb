class CoordinateClothe < ActiveRecord::Base

  has_many :images
  before_destroy :destroy_all_image
  def destroy_all_image
    p "start delete images associated with document"
    images.map(&:destroy)
    p "finish delete images associated with document"
  end

end

