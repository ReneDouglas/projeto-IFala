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

-- Inserir usuários
INSERT INTO usuarios (nome, email, senha, must_change_password, username)
VALUES 
  ('Phaola', 'paixaophaola@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'phaola'),
  ('Renê', 'rene.moraes@ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'rene'),
  ('Joniel', 'jonielmendes237@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'joniel'),
  ('Jonathas', 'jhon.gomes.r@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'jonathas'),
  ('Luis', 'luisthedevmagician@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'luis'),
  ('João', 'joaoandresantana38@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'joao'),
  ('Edilucia', 'ediluciamendesbarbosa@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'edilucia'),
  ('Rafael', 'cacor.2021121tads0038@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'rafael'),
  ('Elissandra', 'elissandrav563@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'elissandra'),
  ('Keyllane', 'keyllaneguede@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'keyllane'),
  ('Mateus', 'matheuscatalao99@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'mateus'),
  ('Guilherme', 'coradodasilva33@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'guilherme'),
  ('Thiago', 'thiagomoraisjacobina@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'thiago'),
  ('Luana', 'cacor.2020121tads0005@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'luana'),
  ('Jordean', 'cacor.2019121tads0028@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE, 'jordean')
ON CONFLICT (email) DO NOTHING;

INSERT INTO usuarios_perfil (usuarios_id, perfil)
SELECT id, 'admin'
FROM usuarios
WHERE email IN (
  'paixaophaola@gmail.com',
  'jonielmendes237@gmail.com',
  'jhon.gomes.r@gmail.com',
  'luisthedevmagician@gmail.com',
  'joaoandresantana38@gmail.com',
  'ediluciamendesbarbosa@gmail.com',
  'cacor.2021121tads0038@aluno.ifpi.edu.br',
  'elissandrav563@gmail.com',
  'keyllaneguede@gmail.com',
  'matheuscatalao99@gmail.com',
  'coradodasilva33@gmail.com',
  'thiagomoraisjacobina@gmail.com',
  'cacor.2020121tads0005@aluno.ifpi.edu.br',
  'cacor.2019121tads0028@aluno.ifpi.edu.br', 
  'rene.moraes@ifpi.edu.br'
)
ON CONFLICT (usuarios_id, perfil) DO NOTHING;