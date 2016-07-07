class AddColumnToImage < ActiveRecord::Migration
  def change
    add_column :images, :coordinate_clothe_id, :integer
  end
end
