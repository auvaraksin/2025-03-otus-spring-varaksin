ALTER TABLE user_profile
    ADD CONSTRAINT validate_email
        CHECK (email ~ '^[\w.%+-]+@[\w.-]+\.(com|ru)$');