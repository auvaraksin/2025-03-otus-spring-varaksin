CREATE TRIGGER update_date
    BEFORE UPDATE
    ON account_details
    FOR EACH ROW
    EXECUTE procedure moddatetime(update_date);