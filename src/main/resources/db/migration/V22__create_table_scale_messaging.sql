CREATE TYPE message_type_content as ENUM ('TEXT', 'IMAGE', 'FILE');
CREATE TYPE message_delivery_status as ENUM ('SENT', 'RECEIVED', 'READ');

CREATE TABLE scale_chat_participant(
    id UUID PRIMARY KEY default gen_random_uuid(),
    event_scale_id uuid NOT NULL,
    member_scale_chat_id uuid NOT NULL,
    CONSTRAINT fk_event_scale_id FOREIGN KEY (event_scale_id) REFERENCES event_scale(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_member_scale_chat_id FOREIGN KEY (member_scale_chat_id) REFERENCES member_event_scale(id) ON DELETE CASCADE,
    unique (event_scale_id, member_scale_chat_id)
);

CREATE TABLE scale_chat_message
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_scale_id UUID NOT NULL,
    member_chat_sender_id UUID NOT NULL,
    content TEXT NOT NULL,
    content_type message_type_content NOT NULL DEFAULT 'TEXT',
    file_type varchar(99),
    sent_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz,
    CONSTRAINT fk_event_scale_id FOREIGN KEY (event_scale_id) REFERENCES event_scale(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_member_sender_id FOREIGN KEY (member_chat_sender_id) REFERENCES scale_chat_participant(id) ON DELETE SET NULL
);

CREATE TABLE message_scale_status(
    id UUID primary key default gen_random_uuid(),
    message_id UUID not null,
    recipíent_chat_id UUID not null,
    status message_delivery_status NOT NULL DEFAULT 'SENT',
    updated_at timestamptz NOT NULL DEFAULT now(),
    CONSTRAINT fk_message_id FOREIGN KEY (message_id) REFERENCES scale_chat_message(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_recipient_chat_id FOREIGN KEY (recipíent_chat_id) REFERENCES scale_chat_participant(id) ON DELETE CASCADE,
    UNIQUE (message_id, recipíent_chat_id)
);

CREATE INDEX idx_message_scale_status_message_id ON message_scale_status(message_id);
CREATE INDEX idx_message_scale_status_recipient_id ON message_scale_status(recipíent_chat_id);