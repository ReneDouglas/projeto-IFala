-- Adiciona as colunas de autenticação à tabela 'usuarios'
ALTER TABLE usuarios
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT TRUE,
ADD COLUMN password_reset_token VARCHAR(255),
ADD COLUMN password_reset_expires TIMESTAMP,
ADD COLUMN username VARCHAR(25) UNIQUE NOT NULL;

-- Adiciona a restrição NOT NULL
ALTER TABLE usuarios
    ALTER COLUMN nome SET NOT NULL,
    ALTER COLUMN senha SET NOT NULL;


-- Adiciona a restrição UNIQUE à coluna 'email'
ALTER TABLE usuarios ADD CONSTRAINT uk_usuarios_email UNIQUE (email);

-- Tabela de associação entre usuários e perfis
CREATE TABLE IF NOT EXISTS usuarios_perfil (
    usuarios_id BIGINT NOT NULL,
    perfil perfis_enum NOT NULL,
    PRIMARY KEY (usuarios_id, perfil),
    FOREIGN KEY (usuarios_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    data_expiracao TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- Inserir usuários
INSERT INTO usuarios (nome, email, senha, must_change_password, username)
VALUES 
  ('Phaola', 'cacor.2024121tads0008@aluno.ifpi.edu.br', '$2a$10$DCKsrhe8UKFWv0hMeTueU..fjNsAicFCTxRNH/4eWuzYDrY1/6n02', TRUE, 'phaola'),
  ('Phaola', 'paixaphaola@gmail.com', '$2a$10$DCKsrhe8UKFWv0hMeTueU..fjNsAicFCTxRNH/4eWuzYDrY1/6n02', TRUE, 'pha')
ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios_perfil (usuarios_id, perfil)
SELECT id, 'admin'
FROM usuarios
WHERE email IN (
  'cacor.2024121tads0008@aluno.ifpi.edu.br',
  'paixaphaola@gmail.com'
)
ON CONFLICT (usuarios_id, perfil) DO NOTHING;