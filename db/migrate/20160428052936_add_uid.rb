class AddUid < ActiveRecord::Migration
  def change
    add_column :projects, :user_id, :integer
    add_column :documents, :user_id, :integer
    add_column :images, :user_id, :integer
    add_column :searches, :user_id, :integer
  end
end
