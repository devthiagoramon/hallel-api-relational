CREATE TABLE events
(
    id                    UUID PRIMARY KEY,
    title                 VARCHAR(255)     NOT NULL,
    description           VARCHAR(255)     NOT NULL,
    date                  TIMESTAMP        NOT NULL,
    local_event_name      VARCHAR(255)     NOT NULL,
    local_event_longitude DOUBLE PRECISION NOT NULL,
    local_event_latitude  DOUBLE PRECISION NOT NULL,
    image_url             VARCHAR(255)     NOT NULL,
    banner_url            VARCHAR(255)     NOT NULL,
    is_important          BOOLEAN          NOT NULL,
    value                 DOUBLE PRECISION NOT NULL
)

CREATE TABLE ministry
(
    id                  UUID PRIMARY KEY,
    title               VARCHAR(255) NOT NULL,
    description         VARCHAR(255),
    image               VARCHAR(255),
    has_repertoire      BOOLEAN,
    ministry_type       VARCHAR(255),
    coordinator_id      UUID,
    vice_coordinator_id UUID,
    CONSTRAINT fk_coordinator FOREIGN KEY (coordinator_id) REFERENCES "user" (id),
    CONSTRAINT fk_vice_coordinator FOREIGN KEY (vice_coordinator_id) REFERENCES "user" (id)
);