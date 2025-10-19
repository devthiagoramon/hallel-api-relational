CREATE TABLE associate
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID UNIQUE    NOT NULL,
    value_association NUMERIC(10, 2) NOT NULL,
    status            VARCHAR(50)    NOT NULL,
    associate_since   TIMESTAMP WITHOUT TIME ZONE,
    renewal_date      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_associate_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id)
            ON DELETE CASCADE
);

CREATE TABLE association_payment
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    associate_id            UUID        NOT NULL,
    reference_month         VARCHAR(7),
    months_covered          INTEGER          DEFAULT 1,
    value_paid              NUMERIC(10, 2),
    paid_date               TIMESTAMP WITHOUT TIME ZONE ,
    mercado_pago_payment_id BIGINT,
    pix_txid                VARCHAR(255),
    payment_method          VARCHAR(50) NOT NULL,
    status                  VARCHAR(50) NOT NULL,

    CONSTRAINT fk_payment_associate
        FOREIGN KEY (associate_id)
            REFERENCES associate (id)
            ON DELETE CASCADE
);

