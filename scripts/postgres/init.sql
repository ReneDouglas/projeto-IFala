-- extensão pgcrypto para geração de UUIDs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ================================
-- ENUMS
-- ================================

-- Categorias de denúncia
CREATE TYPE categorias_enum AS ENUM (
	'celular', 'drogas', 'bullying', 'violencia', 'vandalismo', 'academico', 'outros'
);

-- Status da denúncia
CREATE TYPE status_denuncia_enum AS ENUM (
	'recebido', 'em_analise', 'aguardando', 'resolvido', 'rejeitado'
);

-- Tipos de notificação
CREATE TYPE tipos_notificacao_enum AS ENUM (
	'nova_denuncia', 'nova_mensagem'
);

-- Perfis de usuário
CREATE TYPE perfis_enum AS ENUM (
	'admin', 'anonimo'
);

-- ================================
-- TABELAS
-- ================================

-- Tabela denuncias
CREATE TABLE IF NOT EXISTS denuncias (
	id BIGSERIAL PRIMARY KEY,
	descricao TEXT,
	categoria categorias_enum,
	status status_denuncia_enum,
	motivo_rejeicao TEXT,
	token_acompanhamento UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
	criado_em TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now(),
	alterado_por VARCHAR(255),
	alterado_em TIMESTAMP WITHOUT TIME ZONE
);

-- Tabela acompanhamentos
CREATE TABLE IF NOT EXISTS acompanhamentos (
	id BIGSERIAL PRIMARY KEY,
	autor VARCHAR(255),
	mensagem TEXT,
	denuncia_id BIGINT NOT NULL REFERENCES denuncias(id) ON DELETE CASCADE,
	data_envio TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now()
);

-- Tabela notificacoes
CREATE TABLE IF NOT EXISTS notificacoes (
	id BIGSERIAL PRIMARY KEY,
	conteudo TEXT,
	tipo tipos_notificacao_enum,
	denuncia_id BIGINT REFERENCES denuncias(id) ON DELETE CASCADE,
	lida BOOLEAN DEFAULT FALSE,
	lida_por VARCHAR(255),
	data_envio TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

-- Tabela usuarios
CREATE TABLE IF NOT EXISTS usuarios (
	id BIGSERIAL PRIMARY KEY,
	nome VARCHAR(255),
	email VARCHAR(255),
	senha VARCHAR(255)
);



