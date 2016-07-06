module Uuidable
    extend ActiveSupport::Concern

    # generate uuid before model creation
    included do
        before_create :generate_uuid
    end

    protected

    # generate random uuid
    def generate_uuid
        self.uuid = loop do
            uuid = SecureRandom.urlsafe_base64
            break uuid unless self.class.exists?(uuid: uuid)
        end
    end
end
