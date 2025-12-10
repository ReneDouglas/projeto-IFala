-- Migration: cria tipo e tabela de notificacoes (idempotente)
-- Esta migration usa IF NOT EXISTS para ser segura em bancos já inicializados

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'tipos_notificacao_enum') THEN
        CREATE TYPE tipos_notificacao_enum AS ENUM ('nova_denuncia', 'nova_mensagem');
    END IF;
END$$;

CREATE TABLE IF NOT EXISTS notificacoes (
    id BIGSERIAL PRIMARY KEY,
    conteudo TEXT,
    tipo tipos_notificacao_enum,
    denuncia_id BIGINT REFERENCES denuncias(id) ON DELETE CASCADE,
    lida BOOLEAN DEFAULT FALSE,
    lida_por VARCHAR(255),
    data_envio TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);
