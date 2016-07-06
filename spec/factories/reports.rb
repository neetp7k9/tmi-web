# Read about factories at https://github.com/thoughtbot/factory_girl

FactoryGirl.define do
  factory :report do
    description "MyText"
    user_id 1
    page_now "MyString"
  end
end
