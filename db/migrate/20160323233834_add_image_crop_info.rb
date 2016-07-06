class AddImageCropInfo < ActiveRecord::Migration
  def change
    add_column :images, :crop_info, :text
  end
end
