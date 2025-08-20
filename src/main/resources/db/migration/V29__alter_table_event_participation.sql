ALTER TABLE "event_participation"
    ADD COLUMN pix_txid TEXT;

ALTER TABLE "event_participation"
    ADD COLUMN paid_date timestamptz;