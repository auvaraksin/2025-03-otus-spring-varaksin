CREATE TRIGGER update_date
    BEFORE UPDATE
    ON user_profile
    FOR EACH ROW
    EXECUTE PROCEDURE moddatetime(update_date);