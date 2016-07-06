   class TfidfEngine
      def initialize data_path
        @data_path = data_path        
      end
      def reindex
        load_data
        document_number = @data.size
	#idf
	document_count = Hash.new(0)
	document_word_count = []
	@data.each do |id, text|
	  word_count = Hash.new(0)
	  text.split.each do |word|
	    word_count[word] += 1
	  end
	  word_count.keys.each do |word|
	    document_count[word] += 1
	  end
	  document_word_count.push [id,word_count]
	end
	@idf = {}
	document_count.keys.each do |word|
	  @idf[word] = Math.log(Float(document_number)/document_count[word])
	end
	#tf
	@tfidf = []
	document_word_count.each do |id, word_count|
	  tfidf_document = {}
	  word_count.keys.each do |word|
	    tfidf_document[word] = (Float(word_count[word])/word_count.size) * @idf[word]
	  end
	  @tfidf.push [id ,tfidf_document]
	end
        write_data
        write_index
      end
      def index id, document_text
        load_data
        return if document_text.size == 0
	document_text.upcase!
	@data.push [id, document_text]
        document_number = @data.size
	#idf
	document_count = Hash.new(0)
	document_word_count = []
	@data.each do |id, text|
	  word_count = Hash.new(0)
	  text.split.each do |word|
	    word_count[word] += 1
	  end
	  word_count.keys.each do |word|
	    document_count[word] += 1
	  end
	  document_word_count.push [id,word_count]
	end
	@idf = {}
	document_count.keys.each do |word|
	  @idf[word] = Math.log(Float(document_number)/document_count[word])
	end
	#tf
	@tfidf = []
	document_word_count.each do |id, word_count|
	  tfidf_document = {}
	  word_count.keys.each do |word|
	    tfidf_document[word] = (Float(word_count[word])/word_count.size) * @idf[word]
	  end
	  @tfidf.push [id ,tfidf_document]
	end
        write_data
        write_index
      end
      def search document_text
        load_index
        document_text.upcase!
        p "start to search"
        p @tfidf.size
        p @idf.size
        min_score = -1
        tfidf_target_document = calc_tfidf document_text

        @result = []
        @tfidf.each do |id, tfidf_document|
          p "exec #{id}"
          score = simiality tfidf_document, tfidf_target_document
          p "after exec #{id}"
          min_score = insert id, score, min_score
          p "after inseart #{id}"
        end
        @result.sort! {|a,b| a[1]<=>b[1]}
        response = ""
        @result.reverse.each do |id, score|
          response += "#{id} #{score}\n"
        end
        p response
        return response 
      end
      def insert id, score, min_score
        #bug need fix
        min_score = -1 unless min_score
        max_size = 30
        return min_score if score < min_score
        @result.push [id, score]
        if @result.size < max_size
          min_score = (@result.map{|k| k[1]}).min
        else
          min_id = -1
          min_score = (@result.map{|k| k[1]}).min
          @result.delete_if {|element| element[1]<= min_score}
          min_score = -1
          min_score = (@result.map{|k| k[1]}).min if @result.size > 0
        end
      end  
      def calc_tfidf document_text
        p "start to calc"
        word_count = Hash.new(0)
	document_text.split.each do |word|
	  word_count[word] += 1
	end
	tfidf_document = {}
	word_count.keys.each do |word|
          match_word = match word
	  tfidf_document[match_word] = (Float(word_count[word])/word_count.size) * @idf[match_word]
        end
        p tfidf_document
        return tfidf_document
      end 
      def simiality tfidf_document1, tfidf_document2
        score = 0
        tfidf_document1.keys.each do |word|
          score += tfidf_document1[word]* tfidf_document2[word] if tfidf_document2[word]
        end
        return score
      end 
      def load_index
        p "try to load index"
        return if @tfidf && @idf
        p "start to load tfidf"
        if !(File.exist?(@data_path+"tfidf"))
          @tfidf = {}
        else
          p "data loading"
          @tfidf = Marshal.load(File.open(@data_path+"tfidf","rb"))
        end
        p "start to load idf"
        if !(File.exist?(@data_path+"idf"))
          @idf = {}
        else
          p "data loading"
          @idf = Marshal.load(File.open(@data_path+"idf","rb"))
        end
      end
      def load_data
         p "try to load data"
        if !(File.exist?(@data_path+"data"))
          p "data disappear"
          @data = [] 
        else
          p "data loading"
          @data = Marshal.load(File.open(@data_path+"data","rb"))
        end
      end
      def write_index
        File.open(@data_path+"tfidf","wb") do |file|
          Marshal.dump(@tfidf,file)
        end
        File.open(@data_path+"idf","wb") do |file|
          Marshal.dump(@idf,file)
        end
      end
      def write_data
        File.open(@data_path+"data","wb") do |file|
          Marshal.dump(@data,file)
        end
      end
      def match word
        p "try to match #{word}"
        return word if @idf[word]
        match_word = @idf.keys[0]
        min = string_difference_percent(@idf.keys[0],word)
        @idf.keys.each do |key|
          diff = string_difference_percent(key,word)
          if min > diff
            min = diff
            match_word = key 
          end
        end
        p "match word is #{match_word}"
        return match_word
      end 
      def string_difference_percent a, b
        longer = [a.size, b.size].max
        same = a.each_char.zip(b.each_char).select { |a,b| a == b }.size
        (longer - same) / a.size.to_f
      end  
  end
