class AddColumnImage < ActiveRecord::Migration
  def change
   add_column :images, :clothes_type, :integer
  end
end
