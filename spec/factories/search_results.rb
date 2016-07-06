# Read about factories at https://github.com/thoughtbot/factory_girl

FactoryGirl.define do
  factory :search_result do
    search_id 1
    image_id 1
    result "MyText"
  end
end
