ALTER TABLE client
    ADD CONSTRAINT validate_phone
        CHECK (mobile_phone ~ '^7\d{10}$');