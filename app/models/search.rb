require "uri"
class Search < ActiveRecord::Base
  has_many :search_results
  belongs_to :document
  def search_target
      return [Image.find(document_id).avatar.url(:origin), Image.find(document_id).avatar.url(:thumb), ""]
  end
  def response 
    p "show search result"
    @search_results = search_results
    p @search_results.size
    @response =[]
    result = analyze @search_results
    p result
    #result = result.sort_by {|key, value| -value}.to_h
    result = result.sort_by {|key, value| -value}
    p result
    
    max_nums = 5
    item = 0 
    result.each do |id,score|
      if item >= max_nums
        break
      else
        item += 1
      end
      @response.push [CoordinateClothe.find(id), score]
    end
    return @response
  end
  def analyze search_result
    result = Hash.new(0)
    search_result.each do |search_result|
        max = 1024
      search_result.result.split("\n").each do |line|
        
        items = line.split
        image = Image.find(items[1].to_i)
        #p "image #{items[1].to_i} => #{items[2]}"
        #p "document #{image.document_id} => #{items[2]}"
        result[image.coordinate_clothe_id] += max    if image.coordinate_clothe_id != 0
        max /=2
      end
    end
    return result
  end  
end
