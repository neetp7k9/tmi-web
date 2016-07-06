require 'rails_helper'

RSpec.describe "documents/new", type: :view do
  before(:each) do
    assign(:document, Document.new(
      :name => "MyString",
      :filePath => "MyString",
      :project_id => 1
    ))
  end

  it "renders new document form" do
    render

    assert_select "form[action=?][method=?]", documents_path, "post" do

      assert_select "input#document_name[name=?]", "document[name]"

      assert_select "input#document_filePath[name=?]", "document[filePath]"

      assert_select "input#document_project_id[name=?]", "document[project_id]"
    end
  end
end
