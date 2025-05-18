INSERT INTO "events" (id,
                      title,
                      description,
                      date,
                      local_event_name,
                      local_event_longitude,
                      local_event_latitude,
                      image_url,
                      banner_url,
                      is_important,
                      value)
VALUES ('a1b2c3d4-e5f6-7890-ab12-cdef34567890',
        'Culto Jovem',
        'Um culto especial para os jovens da igreja.',
        '2025-06-15 19:00:00',
        'Igreja Central',
        -46.633309,
        -23.550520,
        'https://example.com/imagens/culto.jpg',
        'https://example.com/banners/culto-banner.jpg',
        true,
        0.00);

INSERT INTO "event_scale" (id,event_id, ministry_id, date)
VALUES (
           '123e4567-e89b-12d3-a456-426614174000',
           'a1b2c3d4-e5f6-7890-ab12-cdef34567890',
           '8675330f-9c78-4c6d-9230-b046b4097392',
           '2025-05-16 14:30:00'
       );