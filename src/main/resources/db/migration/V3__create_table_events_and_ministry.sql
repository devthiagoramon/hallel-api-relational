CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE events
(
    id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
);


CREATE TABLE ministry
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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

CREATE TABLE event_scale
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id    UUID NOT NULL,
    ministry_id UUID NOT NULL,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES "events" (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE audition_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID         NOT NULL,
    title       VARCHAR(100) NOT NULL,
    description VARCHAR(250) NOT NULL,
    date        TIMESTAMP    NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE repertory_ministry
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id   UUID,
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(250) NOT NULL,
    ministry_type VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE music_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(250) NOT NULL,
    letter      TEXT         NOT NULL,
    link        TEXT         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE dance_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(250) NOT NULL,
    link        TEXT         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE video_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(250) NOT NULL,
    link        TEXT         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE
);



CREATE TABLE playlist_repertory
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    music_ministry_id UUID,
    dance_ministry_id UUID,
    ministry_type     VARCHAR(50) NOT NULL,
    CONSTRAINT fk_music_ministry_id FOREIGN KEY (music_ministry_id) REFERENCES "music_ministry" (id) ON DELETE CASCADE ,
    CONSTRAINT fk_dance_ministry_id FOREIGN KEY (dance_ministry_id) REFERENCES "dance_ministry" (id) ON DELETE CASCADE

);

CREATE TABLE member_event_scale
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_scale_id UUID    NOT NULL,
    user_id      UUID    NOT NULL,
    status         VARCHAR NOT NULL,
    reason_absence TEXT    NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE ,
    CONSTRAINT fk_event_scale_id FOREIGN KEY (event_scale_id) REFERENCES "event_scale" (id) ON DELETE CASCADE
);

CREATE TABLE member_ministry
(
    user_id   UUID NOT NULL,
    ministry_id UUID NOT NULL,
    CONSTRAINT fk_member_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE
)