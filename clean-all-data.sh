rm -rf ./public/system/*
rm -rf ./searchServer/index/text/*
rm -rf ./searchServer/index/global/*
rm -rf ./searchServer/index/local/*

bundle exec rake db:reset

curl -X POST -F email=admin@sudomafia.com -F password=friedchicken http://127.0.0.1:9292/api/v1/users
curl -X POST -F email=admin@sudomafia.com -F password=friedchicken http://127.0.0.1:10006/api/v1/users
