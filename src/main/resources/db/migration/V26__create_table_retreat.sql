ALTER TABLE "events"
 ADD COLUMN its_free boolean DEFAULT TRUE;

CREATE TABLE event_transaction
(
    id               UUID PRIMARY KEY,
    event_id       UUID           NOT NULL,
    description      TEXT           NOT NULL,
    transaction_type VARCHAR(50)    NOT NULL,
    value            NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_retreat FOREIGN KEY (event_id) REFERENCES "events" (id) ON DELETE CASCADE
);

INSERT INTO event_transaction (id, description, event_id, transaction_type, value)
VALUES ('f8f17f59-b7b4-4bb6-9b91-4eaa64b8c6c2',
        'Pagamento da inscrição',
        'a1b2c3d4-e5f6-7890-ab12-cdef34567890',
        'ENTRADA',
        250.00),
       ('a2e5f4b3-1c4d-4a83-81b2-915d7c52a5c8',
        'Pagamento da Faxina',
        'a1b2c3d4-e5f6-7890-ab12-cdef34567890',
        'SAIDA',
        75.50);