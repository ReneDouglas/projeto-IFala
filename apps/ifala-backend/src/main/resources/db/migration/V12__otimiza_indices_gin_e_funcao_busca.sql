-- ============================================================================
-- Migration V12: Otimização dos índices GIN e função de busca
-- ============================================================================
-- Esta migração resolve problemas de instabilidade na busca textual causados por:
-- 1. "Pending List" do índice GIN (dados ficam em lista temporária)
-- 2. Estatísticas desatualizadas do planejador de consultas
-- 3. Performance subótima da função com DISTINCT + OR + LEFT JOIN
-- ============================================================================

-- ============================================================================
-- 1. DESABILITAR FASTUPDATE NOS ÍNDICES GIN
-- ============================================================================
-- O índice GIN usa uma "lista de pendências" (fastupdate) para acelerar inserções.
-- Isso pode causar inconsistência: dados recém-inseridos não são encontrados
-- até que a lista seja processada.
-- 
-- Ao desabilitar fastupdate:
-- - Inserções ficam um pouco mais lentas
-- - Buscas sempre retornam dados consistentes e atualizados
-- ============================================================================

ALTER INDEX idx_denuncias_descricao_trgm SET (fastupdate = off);
ALTER INDEX idx_acompanhamentos_mensagem_trgm SET (fastupdate = off);

-- ============================================================================
-- 2. ATUALIZAR ESTATÍSTICAS DAS TABELAS
-- ============================================================================
-- O otimizador do PostgreSQL decide se usa índice ou Sequential Scan baseado
-- nas estatísticas. Estatísticas desatualizadas podem fazer o planejador
-- ignorar os índices mesmo quando seria mais eficiente usá-los.
-- ============================================================================

ANALYZE denuncias;
ANALYZE acompanhamentos;

-- ============================================================================
-- 3. RECRIAR FUNÇÃO DE BUSCA COM UNION (MAIS PERFORMÁTICA)
-- ============================================================================
-- Problemas da versão anterior:
-- - DISTINCT é custoso computacionalmente
-- - OR com LEFT JOIN pode confundir o planejador de consultas
-- - O planejador pode não usar os índices de forma otimizada
--
-- Vantagens da nova versão com UNION:
-- - UNION já elimina duplicatas automaticamente (sem custo extra do DISTINCT)
-- - Cada SELECT pode usar seu respectivo índice de forma independente
-- - O planejador tem mais facilidade para otimizar consultas separadas
-- - Melhor uso dos índices GIN trigram
-- ============================================================================

CREATE OR REPLACE FUNCTION buscar_denuncias_por_texto(termo_busca TEXT)
RETURNS TABLE(denuncia_id BIGINT) AS $$
BEGIN
    RETURN QUERY
    -- Busca direta na descrição da denúncia
    -- Utiliza o índice: idx_denuncias_descricao_trgm
    SELECT d.id 
    FROM denuncias d
    WHERE d.descricao ILIKE '%' || termo_busca || '%'
    
    UNION -- UNION já elimina duplicatas automaticamente
    
    -- Busca nas mensagens de acompanhamento
    -- Utiliza o índice: idx_acompanhamentos_mensagem_trgm
    SELECT a.denuncia_id 
    FROM acompanhamentos a
    WHERE a.mensagem ILIKE '%' || termo_busca || '%';
END;
$$ LANGUAGE plpgsql STABLE;

-- Atualizar comentário da função
COMMENT ON FUNCTION buscar_denuncias_por_texto(TEXT) IS 
'Função otimizada para busca textual em denúncias (V12 - Otimizada).
Busca o termo na descrição da denúncia e nas mensagens de acompanhamento.
Utiliza UNION para melhor performance e uso garantido dos índices GIN trigram.
Configuração fastupdate=off garante consistência imediata dos resultados.
Uso: SELECT * FROM buscar_denuncias_por_texto(''termo'');';

-- ============================================================================
-- NOTAS DE MANUTENÇÃO
-- ============================================================================
-- Se houver problemas de performance no futuro, considerar:
--
-- 1. Executar VACUUM ANALYZE periodicamente:
--    VACUUM ANALYZE denuncias;
--    VACUUM ANALYZE acompanhamentos;
--
-- 2. Verificar se os índices estão sendo usados:
--    EXPLAIN ANALYZE SELECT * FROM buscar_denuncias_por_texto('termo');
--
-- 3. Para busca ainda mais rápida em textos grandes, considerar Full Text Search:
--    - Criar coluna tsvector com to_tsvector('portuguese', descricao)
--    - Criar índice GIN na coluna tsvector
--    - Usar operador @@ com to_tsquery()
-- ============================================================================
