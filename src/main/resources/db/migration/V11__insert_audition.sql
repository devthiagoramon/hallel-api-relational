INSERT INTO audition_ministry (id,
                               ministry_id,
                               event_scale_id,
                               title,
                               description,
                               date)
VALUES ('123e4567-e89b-12d3-a456-426614174000',
        '8675330f-9c78-4c6d-9230-b046b4097392',
        '123e4567-e89b-12d3-a456-426614174000',
        'Ensaio -  Ministério de Louvor',
        'Ensaio para novos membros do ministério de louvor da paróquia.',
        '2025-06-15');



INSERT INTO member_audition_ministry
    (id, audition_ministry_id, user_id, status)
VALUES ('9b23fd61-7f6e-4a5a-9e62-68d3db45e6e1',
        '123e4567-e89b-12d3-a456-426614174000',
        'fdf09dab-a1b1-4c8e-bfa5-cbb80eb40190',
        'PARTICIPANDO'),
       ('2c76a845-df0c-44c8-a430-3d68e3d3d1a1',
        '123e4567-e89b-12d3-a456-426614174000',
        'a78319c9-abd5-48d0-988a-60f421e9dd98',
        'CONVIDADO');