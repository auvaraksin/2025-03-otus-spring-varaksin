CREATE TRIGGER update_date
    BEFORE UPDATE
    ON address
    FOR EACH ROW
    EXECUTE PROCEDURE moddatetime(update_date);