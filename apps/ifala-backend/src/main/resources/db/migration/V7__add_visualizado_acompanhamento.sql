-- Adiciona coluna visualizado à tabela acompanhamentos
-- Para indicar se a mensagem foi visualizada pelo destinatário

ALTER TABLE acompanhamentos 
ADD COLUMN visualizado BOOLEAN DEFAULT FALSE;

-- Atualiza mensagens existentes como não visualizadas
UPDATE acompanhamentos 
SET visualizado = FALSE 
WHERE visualizado IS NULL;
