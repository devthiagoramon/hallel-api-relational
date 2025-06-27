ALTER TABLE "user"
    ADD
    push_notification BOOLEAN;
UPDATE "user" SET push_notification = FALSE;