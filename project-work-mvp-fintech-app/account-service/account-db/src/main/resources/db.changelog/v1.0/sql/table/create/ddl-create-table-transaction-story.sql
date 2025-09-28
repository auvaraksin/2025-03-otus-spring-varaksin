CREATE TABLE transaction_story
(
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    action_status_id INT NOT NULL,
    action_id INT NOT NULL,
    date_create TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_update TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT transaction_story_action_status_fkey FOREIGN KEY (action_status_id) REFERENCES action_status(id),
    CONSTRAINT transaction_story_action_fkey FOREIGN KEY (action_id) REFERENCES action(id),
    CONSTRAINT transaction_story_transaction_fkey FOREIGN KEY (transaction_id) REFERENCES transaction(transaction_id)
);