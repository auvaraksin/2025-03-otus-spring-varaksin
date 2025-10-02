CREATE TRIGGER update_transaction_story_timestamp
    BEFORE UPDATE
    ON transaction_story
    FOR EACH ROW
EXECUTE procedure moddatetime(date_update);
