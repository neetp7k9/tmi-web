require "rsolr"
class Document < ActiveRecord::Base
  belongs_to :project
  has_many :images
  has_attached_file :avatar, :styles => { 
                                         origin: ["", :jpg], 
                                         thumb: ["300x300>", :png] }, 
                             :source_file_options => {:origin => '-density 200'},
                             :default_url => "/images/:style/missing.png"
  validates_attachment :avatar, content_type: { content_type: ["image/jpeg", "image/gif", "image/png", "application/pdf"] }
  before_destroy :destroy_all_image
  def destroy_all_image
    p "start delete images associated with document"
    images.map(&:destroy)
    p "finish delete images associated with document"

    p "delete document text"
    solr = RSolr.connect :url => 'http://127.0.0.1:8000'
    response = solr.get '/delete_text', :params => {:wt=>"xml", :user_id => user_id, :document_id => id}
  end

  def makePages 
    #TODO splite file
     
    #TODO create pages
    
  end   
end
