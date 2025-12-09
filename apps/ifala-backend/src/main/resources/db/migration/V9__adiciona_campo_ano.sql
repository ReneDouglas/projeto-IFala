-- Adiciona coluna 'ano' na tabela 'denunciantes' para armazenar o ano escolar (1º, 2º ou 3º ano)
-- Esta migração refatora o campo turma, separando o ano da turma
ALTER TABLE denunciantes
ADD COLUMN IF NOT EXISTS ano VARCHAR(50);

-- Comentário: O campo 'ano' pode ser NULL pois aplica-se apenas ao ensino médio,
-- não aos cursos superiores que utilizam módulos
