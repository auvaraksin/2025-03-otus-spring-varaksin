ALTER TABLE user_profile
    ADD CONSTRAINT validate_password
        CHECK (password ~ '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$');