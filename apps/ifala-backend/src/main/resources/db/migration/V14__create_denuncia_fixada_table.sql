-- Tabela para armazenar denúncias fixadas por usuários
CREATE TABLE IF NOT EXISTS denuncia_fixada (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    denuncia_id BIGINT NOT NULL,
    fixada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_denuncia_fixada_usuario 
        FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT fk_denuncia_fixada_denuncia 
        FOREIGN KEY (denuncia_id) 
        REFERENCES denuncias(id) 
        ON DELETE CASCADE,
    
    CONSTRAINT uk_usuario_denuncia 
        UNIQUE (usuario_id, denuncia_id)
);

-- Índices para melhorar performance nas consultas
CREATE INDEX IF NOT EXISTS idx_denuncia_fixada_usuario ON denuncia_fixada(usuario_id);
CREATE INDEX IF NOT EXISTS idx_denuncia_fixada_denuncia ON denuncia_fixada(denuncia_id);
