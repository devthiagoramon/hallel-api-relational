CREATE TYPE event_participation_type AS ENUM
('COMUNIDADE', 'OUTRO');

ALTER TABLE event_participation
ADD COLUMN type event_participation_type NOT NULL DEFAULT 'OUTRO';

ALTER TABLE event_participation
ADD COLUMN formation VARCHAR;