class DocumentSerializer < ActiveModel::Serializer
  attributes :id, :name, :filePath, :project_id
end
