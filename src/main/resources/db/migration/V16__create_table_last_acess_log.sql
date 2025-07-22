CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE  TABLE "last_access_log"(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    accessed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    CONSTRAINT fk_last_access_log_user
        FOREIGN KEY (user_id) REFERENCES "user"(id)
            ON DELETE CASCADE
)