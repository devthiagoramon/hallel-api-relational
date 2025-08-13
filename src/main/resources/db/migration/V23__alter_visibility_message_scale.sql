CREATE TYPE message_visibility AS ENUM ('VISIBLE', 'DELETED');

ALTER TABLE scale_chat_message
    ADD COLUMN visibility message_visibility NOT NULL DEFAULT 'VISIBLE';