CREATE TABLE foods
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)   NOT NULL,
    value           DECIMAL(10, 2) NOT NULL,
    stock_quantity  INTEGER        NOT NULL,
    registered_date TIMESTAMP        DEFAULT CURRENT_TIMESTAMP,
    event_id        UUID,
    FOREIGN KEY (event_id) REFERENCES events (id)
);


INSERT INTO foods (id, name, value, stock_quantity, event_id)
VALUES ('fff53621-9d49-4181-bc11-9f499c6b8aad', 'Hambúrguer Gourmet', 15.50, 50,
        '310572e6-0ff1-4f4f-93db-c24e1862caf3'),
       ('168bd98e-91b0-451c-aa6e-82cf12896056', 'Refrigerante (Lata)', 3.00, 100,
        '310572e6-0ff1-4f4f-93db-c24e1862caf3'),
       ('9c1a15c9-95fb-4638-810a-6e849c0f7ec1', 'Batata Frita', 5.50, 75
       , '310572e6-0ff1-4f4f-93db-c24e1862caf3');
