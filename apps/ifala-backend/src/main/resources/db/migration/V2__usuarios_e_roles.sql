-- Cria o tipo ENUM para representar os papéis (autoridades)
CREATE TYPE authorities AS ENUM (
    'ADMIN',
    'GESTOR_INSTITUCIONAL'
);

-- Adiciona as colunas de autenticação à tabela 'usuarios'
ALTER TABLE usuarios
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN password_reset_token VARCHAR(255),
ADD COLUMN password_reset_expires TIMESTAMP;

-- Tabela intermediária que relaciona usuários com suas autoridades
CREATE TABLE IF NOT EXISTS users_authorities (
    user_id BIGINT NOT NULL,
    authority authorities NOT NULL,
    PRIMARY KEY (user_id, authority),
    FOREIGN KEY (user_id) REFERENCES usuarios (id) ON DELETE CASCADE
);