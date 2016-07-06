class ImageSerializer < ActiveModel::Serializer
  attributes :id, :name, :filePath, :project_id, :document_id
end
