-- Migration para adicionar índices de busca textual otimizada
-- Task 218: Atualizar campo de busca para buscar por descrição e mensagens

-- Habilitar extensão pg_trgm para buscas otimizadas com trigrams
-- Esta extensão permite buscas eficientes do tipo LIKE '%termo%'
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Índice GIN para busca na descrição das denúncias
-- Otimiza buscas do tipo: WHERE descricao ILIKE '%termo%'
CREATE INDEX IF NOT EXISTS idx_denuncias_descricao_trgm 
ON denuncias USING GIN (descricao gin_trgm_ops);

-- Índice GIN para busca nas mensagens dos acompanhamentos
-- Otimiza buscas do tipo: WHERE mensagem ILIKE '%termo%'
CREATE INDEX IF NOT EXISTS idx_acompanhamentos_mensagem_trgm 
ON acompanhamentos USING GIN (mensagem gin_trgm_ops);

-- Comentários para documentação
COMMENT ON INDEX idx_denuncias_descricao_trgm IS 'Índice trigram para busca textual otimizada na descrição das denúncias';
COMMENT ON INDEX idx_acompanhamentos_mensagem_trgm IS 'Índice trigram para busca textual otimizada nas mensagens de acompanhamento';

-- ============================================================================
-- FUNÇÃO DE BUSCA OTIMIZADA
-- ============================================================================
-- Função que retorna IDs de denúncias que correspondem ao termo de busca.
-- Busca em: descrição da denúncia E mensagens de acompanhamento.
-- Utiliza os índices GIN com trigrams para performance otimizada.
-- ============================================================================

CREATE OR REPLACE FUNCTION buscar_denuncias_por_texto(termo_busca TEXT)
RETURNS TABLE(denuncia_id BIGINT) AS $$
BEGIN
    -- Retorna IDs únicos de denúncias onde:
    -- 1. A descrição contém o termo de busca, OU
    -- 2. Alguma mensagem de acompanhamento contém o termo de busca
    RETURN QUERY
    SELECT DISTINCT d.id
    FROM denuncias d
    LEFT JOIN acompanhamentos a ON a.denuncia_id = d.id
    WHERE 
        d.descricao ILIKE '%' || termo_busca || '%'
        OR a.mensagem ILIKE '%' || termo_busca || '%';
END;
$$ LANGUAGE plpgsql STABLE;

-- Comentário da função
COMMENT ON FUNCTION buscar_denuncias_por_texto(TEXT) IS 
'Função otimizada para busca textual em denúncias. 
Busca o termo na descrição da denúncia e nas mensagens de acompanhamento.
Utiliza índices GIN com trigrams para performance.
Uso: SELECT * FROM buscar_denuncias_por_texto(''termo'');';
