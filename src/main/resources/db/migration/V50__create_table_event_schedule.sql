DROP TABLE event_schedule;

CREATE TABLE event_schedule
(
    id          UUID default gen_random_uuid() PRIMARY KEY,
    event_id    UUID        NOT NULL,
    description TEXT        NOT NULL,
    date        timestamptz NOT NULL,
    created_at  timestamptz NOT NULL default now(),
    edited_at   timestamptz NULL,
    ministry_id UUID        NULL,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events ON DELETE CASCADE,
    CONSTRAINT fk_ministry FOREIGN KEY (ministry_id) REFERENCES ministry ON DELETE SET NULL
);

CREATE INDEX idx_event_schedule_event_id ON event_schedule (event_id);