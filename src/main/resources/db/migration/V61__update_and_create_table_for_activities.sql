ALTER TABLE event_schedule
    DROP COLUMN ministry_id;

CREATE TABLE event_schedule_ministry
(
    event_schedule_id UUID,
    ministry_id       UUID,
    CONSTRAINT fk_event_schedule FOREIGN KEY (event_schedule_id) REFERENCES event_schedule (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (ministry_id) REFERENCES ministry (id) ON DELETE CASCADE,
    primary key (event_schedule_id, ministry_id)
);

CREATE TABLE event_schedule_user
(
    event_schedule_id UUID,
    user_id           UUID,
    CONSTRAINT fk_event_schedule FOREIGN KEY (event_schedule_id) REFERENCES event_schedule (id) ON DELETE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES "user" (id) ON DELETE CASCADE,
    primary key (event_schedule_id, user_id)
);

CREATE TABLE event_schedule_visitor
(
    event_schedule_id UUID,
    email             varchar(255) NOT NULL,
    name              varchar(255) NOT NULL,
    phone_number      varchar(255),
    date_birth        date,

    CONSTRAINT fk_event_schedule FOREIGN KEY (event_schedule_id) REFERENCES event_schedule (id) ON DELETE CASCADE,

    primary key (event_schedule_id, email)
);