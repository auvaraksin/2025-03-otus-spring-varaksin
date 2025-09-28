CREATE TRIGGER update_date
    BEFORE UPDATE
    ON transaction
    FOR EACH ROW
    EXECUTE procedure moddatetime(update_date);