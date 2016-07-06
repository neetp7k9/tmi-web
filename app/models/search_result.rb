class SearchResult < ActiveRecord::Base
  belongs_to :image
  belongs_to :search
  def search_target
    case search_type  
      when 0 
        return [image.avatar.url(:origin)]
      when 1
        return [image.avatar.url(:origin)]
      when 2 
        return search_text
      when 3 
        return [search.document.avatar.url(:origin)]
    end
  end
  def search_type_text
    case search_type  
      when 0 
        return "global feature (EdgeHistorm) image search" 
      when 1
        return "loacl feature (surf) with codebook 256 clusters image search"
      when 2 
        return "text search "
      when 3 
        return "text search with full image" 
    end
  end
  def response
    info = []
    p search_type
    if search_type == 3 || search_type == 2
      result.split("\n").each do |line|
        items = line.split
        result_image = Document.find(items[0].to_i)
        info.push [result_image.name, result_image, items[1]]
      end
      return info
    else
      result.split("\n").each do |line|
        items = line.split
        result_image = Image.find(items[1].to_i)
        p "image #{items[1].to_i} => #{items[2]}"
        p "document #{result_image.document_id} => #{items[2]}"
        info.push [result_image.document.name, result_image, items[2]]
      end
    end
    return info
  end
end
