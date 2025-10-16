CREATE TRIGGER update_date
    BEFORE UPDATE
    ON passport_data
    FOR EACH ROW
    EXECUTE PROCEDURE moddatetime(update_date);