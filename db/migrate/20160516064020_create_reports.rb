class CreateReports < ActiveRecord::Migration
  def change
    create_table :reports do |t|
      t.text :description
      t.integer :user_id
      t.string :page_now

      t.timestamps null: false
    end
  end
end
