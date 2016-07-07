rm -rf ./public/system/*
rm -rf ./searchServer/index/global/0/*
rm -rf ./searchServer/index/global/1/*
rm -rf ./searchServer/index/global/2/*
rm -rf ./searchServer/index/global/3/*
rm -rf ./searchServer/index/global/4/*
rm -rf ./searchServer/index/local/0/*
rm -rf ./searchServer/index/local/1/*
rm -rf ./searchServer/index/local/2/*
rm -rf ./searchServer/index/local/3/*
rm -rf ./searchServer/index/local/4/*

bundle exec rake db:reset

curl -X POST -F email=admin@sudomafia.com -F password=friedchicken http://127.0.0.1:9292/api/v1/users
curl -X POST -F email=admin@sudomafia.com -F password=friedchicken http://127.0.0.1:10006/api/v1/users
