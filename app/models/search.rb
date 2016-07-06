require "uri"
class Search < ActiveRecord::Base
  has_many :search_results
  belongs_to :document
  def search_target
    @search_results = search_results
    
    if @search_results.size == 1 && @search_results[0].image_id == nil
      if @search_results[0].search_text == nil
        image = document.avatar 
        return [[image.url(:origin), image.url(:thumb), "/search_results/"+@search_results[0].id.to_s]]
      else
        return @search_results[0].search_text
      end
    else
      target = []
      @search_results.each do |search_result|
        target.push [search_result.image.avatar.url(:origin), search_result.image.avatar.url(:thumb), "/search_results/"+search_result.id.to_s] if search_result.search_type == 0
      end
      return target
    end
  end
  def response 
    p "show search result"
    @search_results = search_results
    p @search_results.size
    @response =[]
    if @search_results.size == 1 && @search_results[0].image_id == nil
      @search_results[0].result.split("\n").each do |line|
        items = line.split 
        @response.push [Document.find(items[0].to_i), items[1].to_i, Document.find(items[0].to_i).avatar.url(:origin)]
      end
    else
      crop_info, result = analyze @search_results
      p crop_info
      result = result.sort_by {|key, value| -value}.to_h
      
      max_nums = 5
      item = 0 
      result.each do |id,score|
        if item >= max_nums
          break
        else
          item += 1
        end
        image = create_crop_document id, crop_info[id]
        @response.push [Document.find(id), score, image]
      end
    end
    return @response
  end
  def create_crop_document document_id, crop_data
     image_path = Document.find(document_id).avatar.url(:origin).split("?")[0]
     path = "/tmp/#{document_id}_#{id}.jpg"
     file_path = Rails.root.to_s + "/public/" + path
     FileUtils.cp Rails.root.to_s+"/public"+URI.unescape(image_path), file_path
     cmd = "convert #{file_path} -fill none -stroke red -strokewidth 3"
     crop_data.uniq.each do |data|
       pos = data.split
       pos_string = "#{pos[0]},#{pos[1]} #{pos[2]},#{pos[3]}"
       cmd += " -draw \'stroke-dasharray 5 5 rectangle #{pos_string} \'" 
     end
     p cmd
     system(cmd +  " #{file_path}")  
     return path   
  end
  def analyze search_result
    result = Hash.new(0)
    crop_info = {}
    search_result.each do |search_result|
        max = 1024
      search_result.result.split("\n").each do |line|
        
        items = line.split
        image = Image.find(items[1].to_i)
        #p "image #{items[1].to_i} => #{items[2]}"
        #p "document #{image.document_id} => #{items[2]}"
        result[image.document_id] += max    if image.document_id != 0
        crop_info[image.document_id] ||= []     if image.document_id != 0
        crop_info[image.document_id].push image.crop_info     if image.document_id != 0 && image.crop_info != nil
        max /=2
      end
    end
    return crop_info,result
  end  
end
