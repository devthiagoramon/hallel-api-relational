CREATE TABLE event_invite_batch
(
    id             UUID             NOT NULL PRIMARY KEY,
    event_id       UUID             NOT NULL,
    max_number     INT              NOT NULL,
    value_increase DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);