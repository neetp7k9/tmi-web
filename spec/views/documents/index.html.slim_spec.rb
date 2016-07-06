require 'rails_helper'

RSpec.describe "documents/index", type: :view do
  before(:each) do
    assign(:documents, [
      Document.create!(
        :name => "Name",
        :filePath => "File Path",
        :project_id => 1
      ),
      Document.create!(
        :name => "Name",
        :filePath => "File Path",
        :project_id => 1
      )
    ])
  end

  it "renders a list of documents" do
    render
    assert_select "tr>td", :text => "Name".to_s, :count => 2
    assert_select "tr>td", :text => "File Path".to_s, :count => 2
    assert_select "tr>td", :text => 1.to_s, :count => 2
  end
end
