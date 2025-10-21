ALTER TABLE event_participation
ALTER COLUMN user_id
DROP NOT NULL;

ALTER TABLE event_participation
ADD COLUMN name VARCHAR(255);

ALTER TABLE event_participation
ADD COLUMN email VARCHAR(255);

ALTER TABLE event_participation
ADD COLUMN phone_number varchar(255);

ALTER TABLE event_participation
ADD COLUMN date_birth DATE;

ALTER TABLE event_participation
ADD COLUMN is_married bool;

ALTER TABLE event_participation
ADD CONSTRAINT uc_event_email UNIQUE (event_id, email);

