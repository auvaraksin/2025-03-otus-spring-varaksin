CREATE TRIGGER close_date
    BEFORE UPDATE
    ON account
    FOR EACH ROW
    EXECUTE procedure moddatetime(close_date);