require 'rails_helper'

RSpec.describe "documents/edit", type: :view do
  before(:each) do
    @document = assign(:document, Document.create!(
      :name => "MyString",
      :filePath => "MyString",
      :project_id => 1
    ))
  end

  it "renders the edit document form" do
    render

    assert_select "form[action=?][method=?]", document_path(@document), "post" do

      assert_select "input#document_name[name=?]", "document[name]"

      assert_select "input#document_filePath[name=?]", "document[filePath]"

      assert_select "input#document_project_id[name=?]", "document[project_id]"
    end
  end
end
