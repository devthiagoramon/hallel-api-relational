ALTER TABLE "event_transaction"
    ADD COLUMN date_transaction DATE;

ALTER TABLE "event_participation"
    ADD COLUMN amount_paid DECIMAL DEFAULT 0.0;

ALTER TABLE "event_transaction"
    ADD COLUMN receipt_payment_file_image TEXT;
