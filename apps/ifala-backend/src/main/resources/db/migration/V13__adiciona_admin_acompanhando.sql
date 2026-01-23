-- ============================================================================
-- Migration V13: Adiciona campo para rastrear admin acompanhando denúncia
-- ============================================================================
-- Task: Adicionar tag de acompanhamento de admin no card da denúncia
-- 
-- Este campo armazena o EMAIL do administrador que está acompanhando a denúncia.
-- Apenas um admin pode acompanhar por vez.
-- NULL significa que nenhum admin está acompanhando.
-- ============================================================================

-- Adicionar coluna para armazenar o email do admin que está acompanhando
ALTER TABLE denuncias
ADD COLUMN IF NOT EXISTS admin_acompanhando_email VARCHAR(255) DEFAULT NULL;

-- Índice para otimizar filtros por admin acompanhando
CREATE INDEX IF NOT EXISTS idx_denuncias_admin_acompanhando 
ON denuncias(admin_acompanhando_email);

-- Comentários para documentação
COMMENT ON COLUMN denuncias.admin_acompanhando_email IS 
'Email do administrador que está acompanhando esta denúncia. NULL indica que nenhum admin está acompanhando.';
