ALTER TABLE address
    ADD CONSTRAINT validate_post_code
        CHECK (post_code ~ '^\d{6}$');