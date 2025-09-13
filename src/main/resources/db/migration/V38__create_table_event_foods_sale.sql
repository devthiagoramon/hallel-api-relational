CREATE TABLE event_foods_sale
(
    id                   UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    event_id             UUID           NOT NULL,
    food_id              UUID           NOT NULL,
    event_transaction_id UUID           NOT NULL,
    quantity             INT            NOT NULL,
    price                NUMERIC(10, 2) NOT NULL,
    sold_at              TIMESTAMP      NOT NULL DEFAULT now(),
    CONSTRAINT fk_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_food
        FOREIGN KEY (food_id)
            REFERENCES foods (id)
            ON DELETE CASCADE,
    CONSTRAINT fk_event_transaction
        FOREIGN KEY (event_transaction_id)
            REFERENCES event_transaction (id)
            ON DELETE CASCADE
);

INSERT INTO event_transaction (id, description, event_id, transaction_type, value)
VALUES ('b2eb1d0a-925d-4cf1-9862-08a378666856',
        'Venda de Alimento: Refrigerante (Lata)',
        '310572e6-0ff1-4f4f-93db-c24e1862caf3',
        'ENTRADA',
        5.50);

INSERT INTO event_foods_sale (id, event_id, food_id,event_transaction_id, quantity, price, sold_at)
VALUES ('783b8673-920d-4d25-8220-8e4b83a0e218', '310572e6-0ff1-4f4f-93db-c24e1862caf3',
        '168bd98e-91b0-451c-aa6e-82cf12896056',  'b2eb1d0a-925d-4cf1-9862-08a378666856',1, 5.50,
        '2025-09-12');