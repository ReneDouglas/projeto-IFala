-- Migration para criar tabela de provas (evidências de denúncias)
-- Task 122: Inserir campo upload de provas no cadastro de denúncia

CREATE TABLE IF NOT EXISTS provas (
    id BIGSERIAL PRIMARY KEY,
    denuncia_id BIGINT NOT NULL,
    nome_arquivo VARCHAR(255) NOT NULL,
    caminho_arquivo VARCHAR(500) NOT NULL,
    tamanho_bytes BIGINT NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_provas_denuncia
        FOREIGN KEY (denuncia_id)
        REFERENCES denuncias (id)
        ON DELETE CASCADE
);

-- Índice para melhorar performance de buscas por denúncia
CREATE INDEX idx_provas_denuncia_id ON provas(denuncia_id);

-- Comentários para documentação
COMMENT ON TABLE provas IS 'Armazena metadados das provas/evidências anexadas às denúncias';
COMMENT ON COLUMN provas.denuncia_id IS 'Referência à denúncia associada';
COMMENT ON COLUMN provas.nome_arquivo IS 'Nome do arquivo gerado no formato prova-{id}-{timestamp}';
COMMENT ON COLUMN provas.caminho_arquivo IS 'Caminho completo do arquivo no volume Docker';
COMMENT ON COLUMN provas.tamanho_bytes IS 'Tamanho do arquivo em bytes';
COMMENT ON COLUMN provas.tipo_mime IS 'Tipo MIME do arquivo (image/jpeg, image/png, etc.)';
