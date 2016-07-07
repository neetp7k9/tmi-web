# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20160707145058) do

  create_table "coordinate_clothes", force: :cascade do |t|
    t.datetime "created_at", null: false
    t.datetime "updated_at", null: false
  end

  create_table "documents", force: :cascade do |t|
    t.string   "name",                limit: 255
    t.string   "filePath",            limit: 255
    t.integer  "project_id",          limit: 4
    t.datetime "created_at",                      null: false
    t.datetime "updated_at",                      null: false
    t.string   "avatar_file_name",    limit: 255
    t.string   "avatar_content_type", limit: 255
    t.integer  "avatar_file_size",    limit: 4
    t.datetime "avatar_updated_at"
    t.integer  "user_id",             limit: 4
  end

  create_table "images", force: :cascade do |t|
    t.string   "name",                 limit: 255
    t.string   "filePath",             limit: 255
    t.integer  "project_id",           limit: 4
    t.integer  "document_id",          limit: 4
    t.datetime "created_at",                         null: false
    t.datetime "updated_at",                         null: false
    t.string   "avatar_file_name",     limit: 255
    t.string   "avatar_content_type",  limit: 255
    t.integer  "avatar_file_size",     limit: 4
    t.datetime "avatar_updated_at"
    t.text     "crop_info",            limit: 65535
    t.integer  "user_id",              limit: 4
    t.integer  "coordinate_clothe_id", limit: 4
    t.integer  "clothes_type",         limit: 4
  end

  create_table "projects", force: :cascade do |t|
    t.string   "name",       limit: 255
    t.datetime "created_at",             null: false
    t.datetime "updated_at",             null: false
    t.integer  "user_id",    limit: 4
  end

  create_table "reports", force: :cascade do |t|
    t.text     "description", limit: 65535
    t.integer  "user_id",     limit: 4
    t.string   "page_now",    limit: 255
    t.datetime "created_at",                null: false
    t.datetime "updated_at",                null: false
  end

  create_table "search_results", force: :cascade do |t|
    t.integer  "search_id",   limit: 4
    t.integer  "image_id",    limit: 4
    t.text     "result",      limit: 65535
    t.datetime "created_at",                null: false
    t.datetime "updated_at",                null: false
    t.text     "search_text", limit: 65535
    t.integer  "search_type", limit: 4
  end

  create_table "searches", force: :cascade do |t|
    t.integer  "document_id", limit: 4
    t.datetime "created_at",            null: false
    t.datetime "updated_at",            null: false
    t.integer  "user_id",     limit: 4
  end

  create_table "users", force: :cascade do |t|
    t.string   "uuid",                   limit: 255, default: "", null: false
    t.string   "email",                  limit: 255, default: "", null: false
    t.string   "encrypted_password",     limit: 255, default: "", null: false
    t.string   "reset_password_token",   limit: 255
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          limit: 4,   default: 0,  null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.string   "current_sign_in_ip",     limit: 255
    t.string   "last_sign_in_ip",        limit: 255
    t.datetime "created_at",                                      null: false
    t.datetime "updated_at",                                      null: false
  end

  add_index "users", ["email"], :name => "index_users_on_email", :unique => true
  add_index "users", ["reset_password_token"], :name => "index_users_on_reset_password_token", :unique => true

end
