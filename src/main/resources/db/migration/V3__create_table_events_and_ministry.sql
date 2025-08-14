CREATE
    EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE events
(
    id                    UUID PRIMARY KEY          DEFAULT gen_random_uuid(),
    title                 VARCHAR(255)     NOT NULL,
    description           TEXT             NOT NULL,
    date                  TIMESTAMP        NOT NULL,
    local_event_name      VARCHAR(255)     NOT NULL,
    local_event_longitude DOUBLE PRECISION NOT NULL,
    local_event_latitude  DOUBLE PRECISION NOT NULL,
    image_url             TEXT             NOT NULL,
    banner_url            TEXT             NOT NULL,
    is_important          BOOLEAN          NOT NULL DEFAULT false,
    value                 DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    event_type            TEXT                      DEFAULT 'GERAL',
    schedule              TEXT[]
);


CREATE TABLE ministry
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(255) NOT NULL,
    description         TEXT,
    image               TEXT,
    has_repertoire      BOOLEAN,
    ministry_type       VARCHAR(255),
    coordinator_id      UUID,
    vice_coordinator_id UUID,
    CONSTRAINT fk_coordinator FOREIGN KEY (coordinator_id) REFERENCES "user" (id),
    CONSTRAINT fk_vice_coordinator FOREIGN KEY (vice_coordinator_id) REFERENCES "user" (id)
);

CREATE TABLE member_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,
    ministry_id UUID NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE
);


CREATE TABLE function_ministry
(
    id          uuid default gen_random_uuid(),
    ministry_id uuid         not null,
    name        varchar(255) not null,
    description text         not null,
    icon        text         not null,
    color       text         not null,
    CONSTRAINT fk_ministry FOREIGN KEY (ministry_id) REFERENCES ministry on delete cascade on update cascade,
    primary key (id)
);

CREATE TABLE function_ministry_member
(
    member_ministry_id   uuid not null,
    function_ministry_id uuid not null,
    primary key (member_ministry_id, function_ministry_id),
    CONSTRAINT fk_member_ministry foreign key (member_ministry_id) REFERENCES "member_ministry" (id) on delete cascade,
    CONSTRAINT fk_function_ministry foreign key (function_ministry_id) references function_ministry (id) on delete cascade on update cascade
);


CREATE TABLE event_scale
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id    UUID,
    ministry_id UUID,
    date        DATE,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES ministry (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE audition_ministry
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id    UUID         NOT NULL,
    event_scale_id UUID         NULL,
    title          VARCHAR(100) NOT NULL,
    description    TEXT         NOT NULL,
    date           DATE         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_event_scale_id FOREIGN KEY (event_scale_id) REFERENCES "event_scale" (id) ON DELETE CASCADE ON UPDATE CASCADE

);

CREATE TABLE repertory_ministry
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id   UUID,
    name          VARCHAR(100) NOT NULL,
    description   TEXT         NOT NULL,
    ministry_type VARCHAR(50)  NOT NULL,
    link_playlist TEXT,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE music_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL,
    letter      TEXT         NOT NULL,
    link        TEXT         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE dance_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        TEXT NOT NULL,
    description TEXT NOT NULL,
    link        TEXT NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE

);

CREATE TABLE video_ministry
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ministry_id UUID,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL,
    link        TEXT         NOT NULL,
    CONSTRAINT fk_ministry_id FOREIGN KEY (ministry_id) REFERENCES "ministry" (id) ON DELETE CASCADE
);

CREATE TABLE repertory_music_ministry
(
    repertory_ministry_id UUID,
    music_ministry_id     UUID,
    PRIMARY KEY (repertory_ministry_id, music_ministry_id),
    FOREIGN KEY (repertory_ministry_id) REFERENCES repertory_ministry (id) on delete cascade on update cascade,
    FOREIGN KEY (music_ministry_id) REFERENCES music_ministry (id) on delete cascade on update cascade
);

CREATE TABLE repertory_dance_ministry
(
    repertory_ministry_id UUID,
    dance_ministry_id     UUID,
    PRIMARY KEY (repertory_ministry_id, dance_ministry_id),
    FOREIGN KEY (repertory_ministry_id) REFERENCES repertory_ministry (id) on delete cascade on update cascade,
    FOREIGN KEY (dance_ministry_id) REFERENCES dance_ministry (id) on delete cascade on update cascade
);

CREATE TABLE repertory_video_ministry
(
    repertory_ministry_id UUID NOT NULL,
    video_ministry_id     UUID NOT NULL,
    PRIMARY KEY (repertory_ministry_id, video_ministry_id),
    FOREIGN KEY (repertory_ministry_id) REFERENCES repertory_ministry (id) ON DELETE CASCADE,
    FOREIGN KEY (video_ministry_id) REFERENCES video_ministry (id) ON DELETE CASCADE
);

CREATE TABLE member_event_scale
(
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_scale_id     UUID    NOT NULL,
    member_ministry_id UUID    NOT NULL,
    status             VARCHAR NOT NULL,
    date_view          TIMESTAMP,
    reason_absence     TEXT,
    CONSTRAINT fk_member_ministry_id FOREIGN KEY (member_ministry_id) REFERENCES "member_ministry" (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_scale_id FOREIGN KEY (event_scale_id) REFERENCES "event_scale" (id) ON DELETE CASCADE
);



CREATE TABLE member_audition_ministry
(
    id                   UUID PRIMARY KEY,
    audition_ministry_id UUID NOT NULL,
    member_ministry_id   UUID NOT NULL,
    reason_abscence      TEXT,
    status               TEXT NOT NULL,

    CONSTRAINT fk_audition_ministry FOREIGN KEY (audition_ministry_id)
        REFERENCES "audition_ministry" (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_member_ministry_id FOREIGN KEY (member_ministry_id)
        REFERENCES "member_ministry" (id)
        ON DELETE CASCADE
);
