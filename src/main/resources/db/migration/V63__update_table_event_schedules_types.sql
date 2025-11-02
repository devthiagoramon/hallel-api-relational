CREATE TYPE event_schedule_type as ENUM
    ('PALESTRA', 'MISSA', 'ADORACAO_SACRAMENTO', 'LOUVOR', 'TERCO', 'PROCISSAO', 'CONFISSOES', 'OUTRO');

ALTER TABLE event_schedule
    ADD COLUMN type event_schedule_type NOT NULL
        DEFAULT 'OUTRO';

