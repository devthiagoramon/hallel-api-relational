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
    (id, audition_ministry_id, member_ministry_id, status)
VALUES ('9b23fd61-7f6e-4a5a-9e62-68d3db45e6e1',
        '123e4567-e89b-12d3-a456-426614174000',
        'fbc03b19-c26e-4b27-90cd-08586c8d1470',
        'PARTICIPANDO'),
       ('2c76a845-df0c-44c8-a430-3d68e3d3d1a1',
        '123e4567-e89b-12d3-a456-426614174000',
        'a21575a9-8068-4820-a5b0-2de6e6d6577f',
        'CONVIDADO');