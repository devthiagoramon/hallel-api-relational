UPDATE "events"
SET its_free = CASE
                   WHEN value = 0 THEN TRUE
                   ELSE FALSE
    END;