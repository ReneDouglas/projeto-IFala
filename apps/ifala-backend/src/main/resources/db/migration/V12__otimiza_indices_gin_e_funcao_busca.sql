-- ============================================================================
-- Migration V12: Migração para Full Text Search (FTS) com tsvector
-- ============================================================================
-- Esta migração substitui a estratégia de busca com pg_trgm por Full Text Search
-- nativo do PostgreSQL, que oferece:
-- 1. Busca semântica com suporte ao idioma português
-- 2. Stemming (normalização de palavras: "correndo" -> "correr")
-- 3. Remoção automática de stop words ("de", "o", "a", etc.)
-- 4. Melhor performance em textos longos
-- 5. Suporte a ranking de relevância
-- ============================================================================

-- ============================================================================
-- 1. REMOVER ÍNDICES TRIGRAM (NÃO MAIS NECESSÁRIOS COM FTS)
-- ============================================================================
-- Os índices trigram foram criados no V11, mas não são necessários com FTS.
-- Full Text Search usa seus próprios índices GIN baseados em tsvector.
-- ============================================================================

DROP INDEX IF EXISTS idx_denuncias_descricao_trgm;
DROP INDEX IF EXISTS idx_acompanhamentos_mensagem_trgm;

-- ============================================================================
-- 2. ADICIONAR COLUNAS TSVECTOR PARA BUSCA FULL TEXT
-- ============================================================================
-- Colunas tsvector armazenam os tokens pré-processados do texto,
-- permitindo buscas muito mais rápidas que ILIKE.
-- ============================================================================

ALTER TABLE denuncias 
ADD COLUMN IF NOT EXISTS busca_texto tsvector;

ALTER TABLE acompanhamentos 
ADD COLUMN IF NOT EXISTS busca_texto tsvector;

-- ============================================================================
-- 3. POPULAR COLUNAS TSVECTOR COM DADOS EXISTENTES
-- ============================================================================
-- Usar configuração 'portuguese' para stemming e stop words em português.
-- ============================================================================

UPDATE denuncias 
SET busca_texto = to_tsvector('portuguese', COALESCE(descricao, ''));

UPDATE acompanhamentos 
SET busca_texto = to_tsvector('portuguese', COALESCE(mensagem, ''));

-- ============================================================================
-- 4. CRIAR ÍNDICES GIN NAS COLUNAS TSVECTOR
-- ============================================================================
-- Índices GIN em tsvector são extremamente eficientes para Full Text Search.
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_denuncias_busca_fts 
ON denuncias USING GIN (busca_texto);

CREATE INDEX IF NOT EXISTS idx_acompanhamentos_busca_fts 
ON acompanhamentos USING GIN (busca_texto);

-- Comentários para documentação
COMMENT ON COLUMN denuncias.busca_texto IS 'Coluna tsvector para Full Text Search na descrição';
COMMENT ON COLUMN acompanhamentos.busca_texto IS 'Coluna tsvector para Full Text Search nas mensagens';
COMMENT ON INDEX idx_denuncias_busca_fts IS 'Índice GIN para Full Text Search em denúncias';
COMMENT ON INDEX idx_acompanhamentos_busca_fts IS 'Índice GIN para Full Text Search em acompanhamentos';

-- ============================================================================
-- 5. CRIAR TRIGGERS PARA MANTER TSVECTOR ATUALIZADO
-- ============================================================================
-- Triggers garantem que a coluna tsvector seja atualizada automaticamente
-- sempre que o texto original for inserido ou modificado.
-- ============================================================================

-- Função trigger para denuncias
CREATE OR REPLACE FUNCTION atualizar_busca_texto_denuncias()
RETURNS TRIGGER AS $$
BEGIN
    NEW.busca_texto := to_tsvector('portuguese', COALESCE(NEW.descricao, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Função trigger para acompanhamentos
CREATE OR REPLACE FUNCTION atualizar_busca_texto_acompanhamentos()
RETURNS TRIGGER AS $$
BEGIN
    NEW.busca_texto := to_tsvector('portuguese', COALESCE(NEW.mensagem, ''));
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger na tabela denuncias
DROP TRIGGER IF EXISTS trg_denuncias_busca_texto ON denuncias;
CREATE TRIGGER trg_denuncias_busca_texto
    BEFORE INSERT OR UPDATE OF descricao ON denuncias
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_busca_texto_denuncias();

-- Trigger na tabela acompanhamentos
DROP TRIGGER IF EXISTS trg_acompanhamentos_busca_texto ON acompanhamentos;
CREATE TRIGGER trg_acompanhamentos_busca_texto
    BEFORE INSERT OR UPDATE OF mensagem ON acompanhamentos
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_busca_texto_acompanhamentos();

-- ============================================================================
-- 6. RECRIAR FUNÇÃO DE BUSCA COM FULL TEXT SEARCH
-- ============================================================================
-- A nova função usa:
-- - plainto_tsquery: converte texto do usuário em query de busca
-- - Operador @@: verifica match entre tsvector e tsquery
-- - UNION: combina resultados de denúncias e acompanhamentos
-- ============================================================================

CREATE OR REPLACE FUNCTION buscar_denuncias_por_texto(termo_busca TEXT)
RETURNS TABLE(denuncia_id BIGINT) AS $$
BEGIN
    -- Retorna vazio se termo de busca for nulo ou vazio
    IF termo_busca IS NULL OR TRIM(termo_busca) = '' THEN
        RETURN;
    END IF;

    RETURN QUERY
    -- Busca na descrição da denúncia usando Full Text Search
    -- Utiliza o índice: idx_denuncias_busca_fts
    SELECT d.id 
    FROM denuncias d
    WHERE d.busca_texto @@ plainto_tsquery('portuguese', termo_busca)
    
    UNION -- UNION já elimina duplicatas automaticamente
    
    -- Busca nas mensagens de acompanhamento usando Full Text Search
    -- Utiliza o índice: idx_acompanhamentos_busca_fts
    SELECT a.denuncia_id 
    FROM acompanhamentos a
    WHERE a.busca_texto @@ plainto_tsquery('portuguese', termo_busca);
END;
$$ LANGUAGE plpgsql STABLE;

-- Atualizar comentário da função
COMMENT ON FUNCTION buscar_denuncias_por_texto(TEXT) IS 
'Função otimizada para busca textual em denúncias (V12 - Full Text Search).
Busca o termo na descrição da denúncia e nas mensagens de acompanhamento.
Utiliza Full Text Search do PostgreSQL com configuração portuguesa:
- Stemming: "correndo" encontra "correr", "corrida", etc.
- Stop words: ignora palavras comuns como "de", "o", "a"
- Índices GIN em tsvector para máxima performance
Uso: SELECT * FROM buscar_denuncias_por_texto(''termo'');';

-- ============================================================================
-- 7. ATUALIZAR ESTATÍSTICAS
-- ============================================================================

ANALYZE denuncias;
ANALYZE acompanhamentos;

-- ============================================================================
-- NOTAS DE MANUTENÇÃO
-- ============================================================================
-- 1. A extensão pg_trgm ainda pode ser útil para buscas LIKE fuzzy no futuro,
--    então não foi removida. Se quiser remover:
--    DROP EXTENSION IF EXISTS pg_trgm;
--
-- 2. Para verificar se os índices FTS estão sendo usados:
--    EXPLAIN ANALYZE SELECT * FROM buscar_denuncias_por_texto('termo');
--
-- 3. Para busca com ranking de relevância, usar ts_rank():
--    SELECT d.*, ts_rank(d.busca_texto, query) AS rank
--    FROM denuncias d, plainto_tsquery('portuguese', 'termo') query
--    WHERE d.busca_texto @@ query
--    ORDER BY rank DESC;
--
-- 4. Para busca com operadores avançados (AND, OR, NOT), usar to_tsquery():
--    to_tsquery('portuguese', 'assedio & moral')  -- assedio E moral
--    to_tsquery('portuguese', 'assedio | bullying')  -- assedio OU bullying
-- ============================================================================
