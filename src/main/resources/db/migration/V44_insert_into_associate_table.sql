
INSERT INTO associate (
    id, user_id, value_association, status, associate_since, renewal_date
) VALUES (
             '35e5d0dc-b95b-42c1-ab41-96954e488ea0',
             '35e5d0dc-b95b-42c1-ab41-96954e488ea0',
             50.00,
             'ATIVO',
             '2025-10-09 10:00:00',
             '2025-11-09 10:00:00'
         );


INSERT INTO association_payment (
    associate_id, reference_month, months_covered, value_paid, paid_date,
    mercado_pago_payment_id, pix_txid, payment_method, status
) VALUES (
             '35e5d0dc-b95b-42c1-ab41-96954e488ea0',
             '2025-10',
             1,
             50.00,
             '2025-10-09 09:30:00',
             1234567890123,
             'E20300182022012800000000000000000216',
             'PIX',
             'PAGO'
         );