ALTER TABLE "event_participation"
    ADD COLUMN mercadopago_payment_id BIGINT;

ALTER TABLE "event_transaction"
    ADD COLUMN mercadopago_payment_id BIGINT;
