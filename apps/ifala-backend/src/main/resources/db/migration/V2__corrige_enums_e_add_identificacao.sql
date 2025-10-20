-- 1° converte as colunas que usavam ENUM nativo para VARCHAR
ALTER TABLE denuncias ALTER COLUMN status TYPE VARCHAR(255) USING (status::text);
ALTER TABLE denuncias ALTER COLUMN categoria TYPE VARCHAR(255) USING (categoria::text);

-- 2° adiciona as novas colunas para denúncia identificada (caso queira se identificar)
-- pode recever valores nullos (anonimas)
ALTER TABLE denuncias
ADD COLUMN IF NOT EXISTS deseja_se_identificar BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS nome_completo VARCHAR(255),
ADD COLUMN IF NOT EXISTS email VARCHAR(255),
ADD COLUMN IF NOT EXISTS grau VARCHAR(100),
ADD COLUMN IF NOT EXISTS curso VARCHAR(100),
ADD COLUMN IF NOT EXISTS turma VARCHAR(100);

-- 3° exclui os tipos ENUM que não são mais usados
DROP TYPE IF EXISTS status_denuncia_enum;
DROP TYPE IF EXISTS categorias_enum;