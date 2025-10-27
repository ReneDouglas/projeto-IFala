-- V3__corrige_enums_e_add_identificacao.sql
-- 1° altera as colunas status e categoria para VARCHAR

DO $$
BEGIN
  ALTER TABLE denuncias ALTER COLUMN status TYPE VARCHAR(255) USING (status::text);
EXCEPTION
  WHEN undefined_column OR wrong_object_type THEN
    RAISE NOTICE 'Coluna status não alterada (não existe ou já é VARCHAR).';
END $$;

DO $$
BEGIN
  ALTER TABLE denuncias ALTER COLUMN categoria TYPE VARCHAR(255) USING (categoria::text);
EXCEPTION
  WHEN undefined_column OR wrong_object_type THEN
    RAISE NOTICE 'Coluna categoria não alterada (não existe ou já é VARCHAR).';
END $$;

-- 2° adiciona as novas colunas para denúncia identificada
ALTER TABLE denuncias
ADD COLUMN IF NOT EXISTS deseja_se_identificar BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS nome_completo VARCHAR(255),
ADD COLUMN IF NOT EXISTS email VARCHAR(255),
ADD COLUMN IF NOT EXISTS grau VARCHAR(100),  
ADD COLUMN IF NOT EXISTS curso VARCHAR(100), 
ADD COLUMN IF NOT EXISTS turma VARCHAR(100);

-- 3° exclui os tipos ENUM antigos (se existirem)
DROP TYPE IF EXISTS status_denuncia_enum;
DROP TYPE IF EXISTS categorias_enum;