json.array!(@documents) do |document|
  json.extract! document, :id, :name, :filePath, :project_id
  json.url document_url(document, format: :json)
end
