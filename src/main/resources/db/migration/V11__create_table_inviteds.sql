CREATE TABLE invite_event_scale
(
    id        UUID PRIMARY KEY,
    is_sent   BOOLEAN NOT NULL,
    message   TEXT,
    date_send TIMESTAMP,
    date_edit TIMESTAMP
);

CREATE TABLE guest_invited_event_scale
(
    id                    UUID PRIMARY KEY,
    name                  VARCHAR(255) NOT NULL,
    email                 VARCHAR(255) NOT NULL,
    phone                 VARCHAR(20)  NOT NULL,
    event_scale_id        UUID,
    invite_event_scale_id UUID,
    CONSTRAINT fk_event_scale
        FOREIGN KEY (event_scale_id)
            REFERENCES event_scale (id)
            ON DELETE SET NULL,
    CONSTRAINT fk_invite_event_scale
        FOREIGN KEY (invite_event_scale_id)
            REFERENCES invite_event_scale (id)
            ON DELETE SET NULL
);
INSERT INTO invite_event_scale (id, is_sent, message, date_send, date_edit)
VALUES ('4a0fbead-3c28-4eaa-b487-cb093d897f72',
        true,
        'Convite para participar da escala do próximo culto.',
        '2025-06-11 10:00:00',
        '2025-06-11 11:00:00'),
       ('8e56f29a-5b72-4d47-b95f-d52e8652d44c',
        true,
        'Convite para participar da escala do próximo culto.',
        '2025-06-11 10:00:00',
        '2025-06-11 11:00:00');

INSERT INTO guest_invited_event_scale (id, name, email, phone, event_scale_id, invite_event_scale_id)
VALUES ('f8b278c0-cb98-4b37-bfbd-1935e5934cb7', 'João Silva', 'joao@email.com',
        '21988888888', '09b2415e-ec84-4df3-973b-7ea6cfc32a17', '4a0fbead-3c28-4eaa-b487-cb093d897f72'),
       ('e0c76f3b-7a92-4d03-8b37-b4e42df88f5e', 'Maria Souza', 'maria@email.com',
        '11999999999', '09b2415e-ec84-4df3-973b-7ea6cfc32a17', '8e56f29a-5b72-4d47-b95f-d52e8652d44c');