CREATE TABLE
    limit_event_age_group (
                              id UUID PRIMARY KEY ,
                              event_id UUID NOT NULL,
                              age_group VARCHAR(50) NOT NULL,
                              limit_quantity INT NOT NULL,
                              current_quantity INT NOT NULL,

                              FOREIGN KEY (event_id) REFERENCES "events" ON DELETE CASCADE
);

