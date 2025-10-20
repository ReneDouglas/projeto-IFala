-- Migration to add enum lookup tables and insert enum values

CREATE TABLE IF NOT EXISTS enum_status (
  value varchar(100) PRIMARY KEY,
  label varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS enum_categorias (
  value varchar(100) PRIMARY KEY,
  label varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS enum_graus (
  value varchar(100) PRIMARY KEY,
  label varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS enum_cursos (
  value varchar(100) PRIMARY KEY,
  label varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS enum_turmas (
  value varchar(100) PRIMARY KEY,
  label varchar(255) NOT NULL
);

-- Insert status values
INSERT INTO enum_status (value, label) VALUES
  ('RECEBIDO', 'Recebido'),
  ('EM_ANALISE', 'Em Análise'),
  ('AGUARDANDO', 'Aguardando Informações'),
  ('RESOLVIDO', 'Resolvido'),
  ('REJEITADO', 'Rejeitado')
ON CONFLICT (value) DO NOTHING;

-- Insert categorias
INSERT INTO enum_categorias (value, label) VALUES
  ('BULLYING', 'Bullying e Assédio'),
  ('DROGAS', 'Uso ou Porte de Substâncias Ilícitas'),
  ('VIOLENCIA', 'Violência Física ou Verbal'),
  ('VANDALISMO', 'Vandalismo e Danos ao Patrimônio'),
  ('ACADEMICO', 'Questões Acadêmicas (Fraude, Plágio)'),
  ('OUTROS', 'Outros')
ON CONFLICT (value) DO NOTHING;

-- Insert graus
INSERT INTO enum_graus (value, label) VALUES
  ('MEDIO', 'Médio'),
  ('SUPERIOR', 'Superior')
ON CONFLICT (value) DO NOTHING;

-- Insert cursos
INSERT INTO enum_cursos (value, label) VALUES
  ('ADMINISTRACAO', 'Administração'),
  ('AGROPECUARIA', 'Agropecuária'),
  ('INFORMATICA', 'Informática'),
  ('MEIO_AMBIENTE', 'Meio Ambiente'),
  ('ANALISE_DESENVOLVIMENTO_SISTEMAS', 'Análise e Desenvolvimento de Sistemas'),
  ('LICENCIATURA_MATEMATICA', 'Licenciatura em Matemática'),
  ('LICENCIATURA_FISICA', 'Licenciatura em Física'),
  ('GESTAO_AMBIENTAL', 'Gestão Ambiental')
ON CONFLICT (value) DO NOTHING;

-- Insert turmas
INSERT INTO enum_turmas (value, label) VALUES
  ('ANO1_A', '1 Ano A'),
  ('ANO1_B', '1 Ano B'),
  ('ANO2_A', '2 Ano A'),
  ('ANO2_B', '2 Ano B'),
  ('ANO3_A', '3 Ano A'),
  ('ANO3_B', '3 Ano B'),
  ('MODULO_I', 'Módulo I'),
  ('MODULO_II', 'Módulo II'),
  ('MODULO_III', 'Módulo III'),
  ('MODULO_IV', 'Módulo IV'),
  ('MODULO_V', 'Módulo V'),
  ('MODULO_VI', 'Módulo VI'),
  ('MODULO_VII', 'Módulo VII'),
  ('MODULO_VIII', 'Módulo VIII')
ON CONFLICT (value) DO NOTHING;
