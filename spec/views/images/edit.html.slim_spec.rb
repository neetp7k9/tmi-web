require 'rails_helper'

RSpec.describe "images/edit", type: :view do
  before(:each) do
    @image = assign(:image, Image.create!(
      :name => "MyString",
      :filePath => "MyString",
      :project_id => 1,
      :document_id => 1
    ))
  end

  it "renders the edit image form" do
    render

    assert_select "form[action=?][method=?]", image_path(@image), "post" do

      assert_select "input#image_name[name=?]", "image[name]"

      assert_select "input#image_filePath[name=?]", "image[filePath]"

      assert_select "input#image_project_id[name=?]", "image[project_id]"

      assert_select "input#image_document_id[name=?]", "image[document_id]"
    end
  end
end
