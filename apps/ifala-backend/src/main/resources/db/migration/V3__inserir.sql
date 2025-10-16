-- Inserir usuários
INSERT INTO usuarios (nome, email, senha, must_change_password)
VALUES 
  ('Phaola', 'paixaophaola@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Renê', 'rene.moraes@ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Joniel', 'jonielmendes237@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Jonathas', 'jhon.gomes.r@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Luis', 'luisthedevmagician@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('João', 'joaoandresantana38@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Edilucia', 'ediluciamendesbarbosa@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Rafael', 'cacor.2021121tads0038@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Elissandra', 'elissandrav563@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Keyllane', 'keyllaneguede@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Mateus', 'matheuscatalao99@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Guilherme', 'coradodasilva33@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Thiago', 'thiagomoraisjacobina@gmail.com', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Luana', 'cacor.2020121tads0005@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE),
  ('Jordean', 'cacor.2019121tads0028@aluno.ifpi.edu.br', '$2a$10$5R.283.kkRx0DJZBkKP1E.7uiSrYwXB5wTM1kUhYTPAiSkQoJRFQy', TRUE)
ON CONFLICT (email) DO NOTHING;

-- Associar autoridade 'gestor_institucional' a todos os usuários (exceto o admin principal)
INSERT INTO users_authorities (user_id, authority)
SELECT id, 'gestor_institucional'
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
  'cacor.2019121tads0028@aluno.ifpi.edu.br'
)
ON CONFLICT (user_id, authority) DO NOTHING;


INSERT INTO users_authorities (user_id, authority)
SELECT id, 'admin'
FROM usuarios
WHERE email = 'rene.moraes@ifpi.edu.br'
ON CONFLICT (user_id, authority) DO NOTHING;
