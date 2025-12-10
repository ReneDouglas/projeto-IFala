-- 1° cria a nova tabela para 'denunciantes'
CREATE TABLE IF NOT EXISTS denunciantes (
    id BIGSERIAL PRIMARY KEY,
    nome_completo VARCHAR(255),
    email VARCHAR(255),
    grau VARCHAR(100),   
    curso VARCHAR(100),  
    turma VARCHAR(100)   
);

-- 2° adiciona a coluna de chave estrangeira na tabela 'denuncias'
ALTER TABLE denuncias
ADD COLUMN IF NOT EXISTS denunciante_id BIGINT;

-- 2.1 adiciona constraint UNIQUE para garantir 1:1
ALTER TABLE denuncias
ADD CONSTRAINT uk_denuncia_denunciante_id UNIQUE (denunciante_id);

-- 2.2 adiciona a constraint de chave estrangeira
ALTER TABLE denuncias
ADD CONSTRAINT fk_denuncia_denunciante
FOREIGN KEY (denunciante_id) REFERENCES denunciantes(id)
ON DELETE CASCADE; -- ON DELETE CASCADE para remover o denunciante junto com a denúncia

-- 3° remove as colunas antigas da tabela 'denuncias'
ALTER TABLE denuncias
DROP COLUMN IF EXISTS nome_completo,
DROP COLUMN IF EXISTS email,
DROP COLUMN IF EXISTS grau,
DROP COLUMN IF EXISTS curso,
DROP COLUMN IF EXISTS turma;