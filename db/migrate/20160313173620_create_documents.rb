class CreateDocuments < ActiveRecord::Migration
  def change
    create_table :documents do |t|
      t.string :name
      t.string :filePath
      t.integer :project_id

      t.timestamps null: false
    end
  end
end
