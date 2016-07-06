class CreateSearchResults < ActiveRecord::Migration
  def change
    create_table :search_results do |t|
      t.integer :search_id
      t.integer :image_id
      t.text :result

      t.timestamps null: false
    end
  end
end
