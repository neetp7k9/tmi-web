require 'rails_helper'

RSpec.describe "images/index", type: :view do
  before(:each) do
    assign(:images, [
      Image.create!(
        :name => "Name",
        :filePath => "File Path",
        :project_id => 1,
        :document_id => 2
      ),
      Image.create!(
        :name => "Name",
        :filePath => "File Path",
        :project_id => 1,
        :document_id => 2
      )
    ])
  end

  it "renders a list of images" do
    render
    assert_select "tr>td", :text => "Name".to_s, :count => 2
    assert_select "tr>td", :text => "File Path".to_s, :count => 2
    assert_select "tr>td", :text => 1.to_s, :count => 2
    assert_select "tr>td", :text => 2.to_s, :count => 2
  end
end