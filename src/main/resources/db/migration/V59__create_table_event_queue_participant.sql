CREATE TABLE event_queue_participant (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    event_participation_id UUID NOT NULL,
    notified boolean NULL DEFAULT FALSE,
    queued_at TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW() NOT NULL,
    CONSTRAINT fk_event_queue
        FOREIGN KEY(event_id)
            REFERENCES "events"(id)
            ON DELETE CASCADE,
    CONSTRAINT fk_participant
        FOREIGN KEY(event_participation_id)
            REFERENCES "event_participation"(id)
            ON DELETE CASCADE
);