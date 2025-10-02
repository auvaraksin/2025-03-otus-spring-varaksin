ALTER TABLE user_profile
    ADD CONSTRAINT validate_authorization_pin
        CHECK (authorization_pin ~ '^\d{4}$');