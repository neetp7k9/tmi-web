class AddAttachmentAvatarToDocuments < ActiveRecord::Migration
  def self.up
    change_table :documents do |t|
      t.attachment :avatar
    end
  end

  def self.down
    remove_attachment :documents, :avatar
  end
end
