CREATE TABLE event_scale_repertory
(
    event_scale_id        UUID,
    repertory_ministry_id UUID,
    CONSTRAINT fk_event_scale FOREIGN KEY (event_scale_id)
        REFERENCES event_scale (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_repertory_ministry FOREIGN KEY (repertory_ministry_id)
        REFERENCES repertory_ministry (id)
        ON DELETE CASCADE,
    primary key (event_scale_id, repertory_ministry_id)
);
