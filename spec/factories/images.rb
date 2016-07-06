# Read about factories at https://github.com/thoughtbot/factory_girl

FactoryGirl.define do
  factory :image do
    name "MyString"
    filePath "MyString"
    project_id 1
    document_id 1
  end
end
