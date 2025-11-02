CREATE TABLE event_invite_batch
(
    id         UUID NOT NULL PRIMARY KEY,
    event_id   UUID NOT NULL,
    max_number INT  NOT NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);