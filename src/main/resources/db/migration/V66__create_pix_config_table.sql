CREATE TABLE pix_config (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chave_pix VARCHAR(255) NOT NULL,
    tipo_chave VARCHAR(50) NOT NULL,
    nome_banco VARCHAR(100) NOT NULL,
    nome_recebedor VARCHAR(150) NOT NULL,
    descricao VARCHAR(500),
    mercado_pago_access_token VARCHAR(500),
    ativo BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMP
);
