CREATE TABLE event_schedule
(
    event_id UUID NOT NULL,
    activity   TEXT,
    FOREIGN KEY (event_id) REFERENCES "events" (id) ON DELETE CASCADE
);