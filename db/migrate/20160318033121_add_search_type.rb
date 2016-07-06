class AddSearchType < ActiveRecord::Migration
  def change
    add_column :search_results, :search_type, :integer
  end
end
