CREATE TRIGGER update_date
    BEFORE UPDATE
    ON client
    FOR EACH ROW
    EXECUTE PROCEDURE moddatetime(update_date);