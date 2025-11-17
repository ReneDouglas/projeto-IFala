Relatório de Teste – IFala / Cadastro de Denúncias Responsável: Pedro Henrique
Vogado Maia Data: 16/11/2025 Versão Testada: development (último pull)

1. Situação do Ambiente Durante a tentativa de inicializar o ambiente com o
   comando: docker compose up -d --build

O backend não compilou, impossibilitando a execução dos testes da funcionalidade
Cadastro de Denúncias. A falha ocorreu durante a etapa de compilação do Maven,
impedindo que o container ifala-backend fosse construído corretamente.

2. Status Geral da Execução dos Testes Status: BLOQUEADO — devido a uma falha
   crítica no processo de build do backend. Como o backend não inicia, não é
   possível acessar a aplicação e testar o fluxo de cadastro de denúncias.

3. Detalhamento do Bug de Build ID do Bug: BUG-102-BUILD-01 Título: [Backend]
   Falha de compilação ao buildar o backend do IFala Severidade / Prioridade
   Severidade: CRÍTICA Prioridade: URGENTE

Passos para Reproduzir

1. Abrir o projeto projeto-IFala na branch development.
2. Rodar o comando: docker compose up -d --build
3. Aguardar a etapa de compilação do backend.
4. Observar o erro retornado pelo Maven.

Resultado Esperado O backend deveria compilar sem erros, gerar o artefato com
sucesso e permitir que o container do backend seja iniciado, possibilitando o
teste da funcionalidade de cadastro de denúncias. Resultado Atual

O Maven interrompe o processo de build com diversos erros de compilação,
indicando incompatibilidade entre o nome das classes e o nome dos arquivos nos
diretórios do módulo de autenticação. Isso impede o backend de iniciar e
bloqueia completamente a execução dos testes. Evidências

Exemplos dos erros detectados:
/app/src/main/java/br/edu/ifpi/ifala/autenticacao/dto/LoginRequestDto.java:[18,8]
class LoginRequestDTO is public, should be declared in a file named
LoginRequestDTO.java
/app/src/main/java/br/edu/ifpi/ifala/autenticacao/dto/RegistroRequestDto.java:[20,8]
class RegistroRequestDTO is public, should be declared in a file named
RegistroRequestDTO.java Outros arquivos afetados incluem
MudarSenhaRequestDto.java, UsuarioResponseDto.java, RefreshTokenRequestDto.java,
LoginResponseDto.java e TokenDataDto.java.

Status do Bug Status: NOVO
