ALTER TABLE event_participation
    ADD COLUMN event_invite_id UUID;

ALTER TABLE event_participation
    ADD CONSTRAINT fk_event_invite
        FOREIGN KEY (event_invite_id)
            REFERENCES event_invite (id);