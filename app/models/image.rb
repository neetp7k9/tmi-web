require "rsolr"
class Image < ActiveRecord::Base
  has_attached_file :avatar, styles: { 
                                       origin: ["", :jpg],medium: ["300x300>", :png],
                                       thumb: ["100x100>", :png] },
                             default_url: "/images/:style/missing.png"

  validates_attachment :avatar, content_type: { content_type: ["image/jpeg", "image/jpg", "image/png", "application/pdf"] }

  belongs_to :project
  belongs_to :document

  before_destroy :destroy_image
  def destroy_image
    p "destroy image"
    solr = RSolr.connect :url => 'http://127.0.0.1:8000'
    response = solr.get '/delete', :params => {:wt=>"xml", :user_id => user_id, :image_id => id}
  end
end

