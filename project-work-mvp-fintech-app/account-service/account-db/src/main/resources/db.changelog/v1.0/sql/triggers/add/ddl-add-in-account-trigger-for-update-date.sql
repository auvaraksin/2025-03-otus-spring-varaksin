CREATE TRIGGER update_date
    BEFORE UPDATE
    ON account
    FOR EACH ROW
    EXECUTE procedure moddatetime(update_date);