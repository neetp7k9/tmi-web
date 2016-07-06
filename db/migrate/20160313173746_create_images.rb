class CreateImages < ActiveRecord::Migration
  def change
    create_table :images do |t|
      t.string :name
      t.string :filePath
      t.integer :project_id
      t.integer :document_id

      t.timestamps null: false
    end
  end
end
