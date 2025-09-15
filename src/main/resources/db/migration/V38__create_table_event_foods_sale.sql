-- PASSO 1: Criar a tabela 'pai' primeiro.
CREATE TABLE food_transactions
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    value                   NUMERIC(10, 2)           NOT NULL,
    description             VARCHAR(255),
    event_id                UUID,
    mercado_pago_payment_id BIGINT,
    status                  VARCHAR(50)              NOT NULL,
    date_transaction        TIMESTAMP WITH TIME ZONE NOT NULL,
    event_transaction_id    UUID UNIQUE,

    CONSTRAINT fk_food_transactions_to_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
            ON DELETE SET NULL,

    CONSTRAINT fk_food_transactions_to_event_transaction
        FOREIGN KEY (event_transaction_id)
            REFERENCES event_transaction (id)
            ON DELETE SET NULL
);

-- PASSO 2: Criar a tabela 'filha' depois.
CREATE TABLE food_sale_items
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id            UUID,
    food_id             UUID           NOT NULL,
    quantity            INT            NOT NULL,
    price               NUMERIC(10, 2) NOT NULL,
    sold_at             TIMESTAMP,
    food_transaction_id UUID           NOT NULL,

    CONSTRAINT fk_food_sale_items_to_event
        FOREIGN KEY (event_id)
            REFERENCES events (id)
            ON DELETE SET NULL,

    CONSTRAINT fk_food_sale_items_to_food
        FOREIGN KEY (food_id)
            REFERENCES foods (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_food_sale_items_to_food_transaction
        FOREIGN KEY (food_transaction_id)
            REFERENCES food_transactions (id)
            ON DELETE CASCADE
);

INSERT INTO event_transaction(id, description, transaction_type,value,date_transaction,is_editable,event_id)
values ('4632b7cb-5b6a-4763-9f88-962255f49779','Venda de Alimento: 1 refrigerante','ENTRADA',5.50,'2025-09-12 10:00:00-04',
        true,'310572e6-0ff1-4f4f-93db-c24e1862caf3');

INSERT INTO food_transactions (id, value, status, date_transaction, event_id, description)
VALUES ('a1b2c3d4-e5f6-7890-1234-567890abcdef', 5.50, 'PAGO', '2025-09-12 10:00:00-04',
        '310572e6-0ff1-4f4f-93db-c24e1862caf3', 'Venda Teste');

INSERT INTO food_sale_items (id, event_id, food_id, food_transaction_id, quantity, price, sold_at)
VALUES ('783b8673-920d-4d25-8220-8e4b83a0e218', '310572e6-0ff1-4f4f-93db-c24e1862caf3',
        '168bd98e-91b0-451c-aa6e-82cf12896056', 'a1b2c3d4-e5f6-7890-1234-567890abcdef', 1, 5.50,
        '2025-09-12');