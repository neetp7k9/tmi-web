class Project < ActiveRecord::Base
  has_many :documents
  belongs_to :user
  before_destroy :destroy_all_document

  def destroy_all_document
    p "start to delete all document"
    documents.map(&:destroy)
    p "finish delete all document"
  end
end
