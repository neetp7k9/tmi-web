class AddTextToSearchResult < ActiveRecord::Migration
  def change
    add_column :search_results, :search_text, :text
  end
end
