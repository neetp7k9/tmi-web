class CreateCoordinateClothes < ActiveRecord::Migration
  def change
    create_table :coordinate_clothes do |t|

      t.timestamps null: false
    end
  end
end
