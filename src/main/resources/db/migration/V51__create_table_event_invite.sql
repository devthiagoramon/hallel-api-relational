CREATE TABLE event_invite(
    id UUID default gen_random_uuid() PRIMARY KEY,
    event_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    value DOUBLE PRECISION NOT NULL DEFAULT 0.0
);

CREATE INDEX idx_event_invites_event_id ON event_invite(event_id);
