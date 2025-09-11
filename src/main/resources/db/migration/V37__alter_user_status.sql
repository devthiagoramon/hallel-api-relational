CREATE TYPE user_status as ENUM ('ENABLED', 'DISABLED');

ALTER TABLE "user"
    ADD COLUMN status user_status not null default 'ENABLED';
