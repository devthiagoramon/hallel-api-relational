CREATE
EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE event_participation
(
    id                                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                            UUID        NOT NULL,
    event_id                           UUID        NOT NULL,
    status_payment_event_participation VARCHAR(20) NOT NULL,
    has_participated                   BOOLEAN     NOT NULL,
    user_function_in_event             VARCHAR(20) NOT NULL,
    CONSTRAINT fk_event_participation_user FOREIGN KEY (user_id)
        REFERENCES "user"(id),
    CONSTRAINT fk_event_participation_event FOREIGN KEY (event_id)
        REFERENCES events(id) ON DELETE CASCADE

);

INSERT INTO event_participation (id,
                                 user_id,
                                 event_id,
                                 status_payment_event_participation,
                                 has_participated,
                                 user_function_in_event)
VALUES ('4f8b7c60-9d5c-4c14-b9b1-2de6f735ea57',
        'a78319c9-abd5-48d0-988a-60f421e9dd98',
        'a1b2c3d4-e5f6-7890-ab12-cdef34567890',
        'PENDENTE',
        FALSE,
        'VOLUNTARIO');