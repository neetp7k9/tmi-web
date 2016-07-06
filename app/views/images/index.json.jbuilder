json.array!(@images) do |image|
  json.extract! image, :id, :name, :filePath, :project_id, :document_id
  json.url image_url(image, format: :json)
end
