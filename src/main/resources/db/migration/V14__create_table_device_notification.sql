CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE device_notification
(
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id        uuid unique NOT NULL,
    fcm_token        TEXT        NOT NULL,
    ip_address       VARCHAR(20) NOT NULL,
    operation_system VARCHAR(20) NOT NULL
);

CREATE TABLE user_device_notification
(
    user_id                UUID,
    device_notification_id UUID,
    primary key (user_id, device_notification_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id)
            ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_device_notification
        FOREIGN KEY (device_notification_id)
            REFERENCES device_notification (id)
            ON UPDATE CASCADE ON DELETE CASCADE
);