package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.acompanhamento.AcompanhamentoRepository;
import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.AtualizarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.CriarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DadosDeIdentificacaoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaAdminResponseDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenuncianteResponseDto;
import br.edu.ifpi.ifala.notificacao.NotificacaoExternaService;
import br.edu.ifpi.ifala.prova.ProvaService;
import br.edu.ifpi.ifala.security.recaptcha.RecaptchaService;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Classe de serviço responsável por manipular operações relacionadas a
 * denúncias.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 * @author Phaola
 */

@Service
@Transactional
public class DenunciaService {

  private static final Logger log = LoggerFactory.getLogger(DenunciaService.class);

  private final DenunciaRepository denunciaRepository;
  private final AcompanhamentoRepository acompanhamentoRepository;
  private final RecaptchaService recaptchaService;
  private final NotificacaoExternaService notificacaoExternaService;
  private final ProvaService provaService;
  private final PolicyFactory policy;
  private final Double score = 0.2;

  // A SER USADO DEPOIS QUE O RECAPTCHA ESTIVER FUNCIONANDO EM PRODUÇÃO
  // private final RecaptchaService recaptchaService;

  // public DenunciaService(DenunciaRepository denunciaRepository,
  // AcompanhamentoRepository acompanhamentoRepository,
  // RecaptchaService recaptchaService) {
  // this.denunciaRepository = denunciaRepository;
  // this.acompanhamentoRepository = acompanhamentoRepository;
  // this.recaptchaService = recaptchaService;
  // }

  public DenunciaService(DenunciaRepository denunciaRepository,
      AcompanhamentoRepository acompanhamentoRepository, RecaptchaService recaptchaService,
      NotificacaoExternaService notificacaoExternaService, ProvaService provaService) {
    this.denunciaRepository = denunciaRepository;
    this.acompanhamentoRepository = acompanhamentoRepository;
    this.recaptchaService = recaptchaService;
    this.notificacaoExternaService = notificacaoExternaService;
    this.provaService = provaService;
    this.policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
  }

  public DenunciaResponseDto criarDenuncia(CriarDenunciaDto dto) {
    return criarDenuncia(dto, null);
  }

  public DenunciaResponseDto criarDenuncia(CriarDenunciaDto dto, List<MultipartFile> provas) {

    log.info("Iniciando validação do reCAPTCHA para nova denúncia.");

    boolean isRecaptchaValid = recaptchaService.validarToken(dto.recaptchaToken(), "denuncia", score);

    if (!isRecaptchaValid) {
      log.warn("Falha na validação do reCAPTCHA para nova denúncia.");
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Falha na validação do ReCaptcha.");
    }

    log.info("reCAPTCHA validado com sucesso para nova denúncia.");

    Denuncia novaDenuncia = new Denuncia();
    novaDenuncia.setDescricao(policy.sanitize(dto.descricao()));

    novaDenuncia.setCategoria(dto.categoria());

    if (Boolean.TRUE.equals(dto.desejaSeIdentificar()) && dto.dadosDeIdentificacao() != null) {

      log.info("Processando criação de denúncia identificada.");
      novaDenuncia.setDesejaSeIdentificar(true);

      Denunciante denunciante = new Denunciante();
      DadosDeIdentificacaoDto idDto = dto.dadosDeIdentificacao();

      denunciante.setNomeCompleto(policy.sanitize(idDto.nomeCompleto()));
      denunciante.setEmail(idDto.email().trim()); // Email não deve ser sanitizado
      denunciante.setGrau(idDto.grau());
      denunciante.setCurso(idDto.curso());
      denunciante.setAno(idDto.ano());
      denunciante.setTurma(idDto.turma());

      novaDenuncia.setDenunciante(denunciante);

    } else {
      log.info("Processando criação de denúncia anônima.");
      novaDenuncia.setDesejaSeIdentificar(false);
      novaDenuncia.setDenunciante(null);
    }

    Denuncia denunciaSalva = denunciaRepository.save(novaDenuncia);

    log.info("Denúncia salva com sucesso");
    log.info("Denúncia criada com ID: {}", denunciaSalva.getId());
    log.info("Token de acompanhamento gerado: {}",
        maskToken(denunciaSalva.getTokenAcompanhamento()));

    // Processar upload de provas se houver
    if (provas != null && !provas.isEmpty()) {
      try {
        log.info("Processando upload de {} provas para denúncia ID {}", provas.size(),
            denunciaSalva.getId());
        provaService.salvarProvas(denunciaSalva, provas);
        log.info("Provas salvas com sucesso para denúncia ID {}", denunciaSalva.getId());
      } catch (Exception e) {
        log.error("Erro ao salvar provas da denúncia ID {}: {}", denunciaSalva.getId(),
            e.getMessage(), e);
        // Não falhar a criação da denúncia se o upload de provas falhar
        // mas logar o erro para investigação
      }
    }

    // Criar automaticamente o primeiro acompanhamento com o relato da denúncia
    Acompanhamento primeiroAcompanhamento = new Acompanhamento();
    primeiroAcompanhamento.setMensagem(denunciaSalva.getDescricao());
    primeiroAcompanhamento.setDenuncia(denunciaSalva);
    primeiroAcompanhamento.setAutor(Perfis.ANONIMO);
    acompanhamentoRepository.save(primeiroAcompanhamento);
    log.info("Primeiro acompanhamento criado automaticamente com o relato da denúncia.");

    // Notificar administradores/usuários externos sobre a nova denúncia
    try {
      notificacaoExternaService.notificarNovaDenuncia(denunciaSalva);
      log.info("Notificação externa enviada sobre nova denúncia ID {}", denunciaSalva.getId());
    } catch (Exception e) {
      log.error("Erro ao notificar administradores sobre nova denúncia ID {}: {}",
          denunciaSalva.getId(), e.getMessage(), e);
    }

    return mapToDenunciaResponseDto(denunciaSalva);
  }

  @Transactional
  public Optional<DenunciaResponseDto> consultarPorTokenAcompanhamento(UUID tokenAcompanhamento) {
    return denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento).map(denuncia -> {
      // Marcar mensagens do ADMIN como visualizadas quando usuário acessa
      acompanhamentoRepository.marcarComoVisualizadoPorDenunciaEAutor(denuncia.getId(),
          Perfis.ADMIN);
      return mapToDenunciaResponseDto(denuncia);
    });
  }

  /*
   * tipo Page é uma interface do Spring Data que encapsula uma página de dados
   * Pageable é uma
   * interface que define a paginação e ordenação Specification é uma interface do
   * Spring Data JPA
   * que permite construir consultas dinamicamente predicate é uma condição usada
   * em consultas para
   * filtrar resultados
   */

  /**
   * Lista todas as denúncias com filtros e paginação.
   *
   * @param search filtro de busca por token
   * @param status filtro de status
   * @param categoria filtro de categoria
   * @param pageable informações de paginação
   * @return página de denúncias
   */
  @Transactional(readOnly = true)
  public Page<DenunciaAdminResponseDto> listarTodas(String search, Status status,
      Categorias categoria, Pageable pageable) {

    log.info("Iniciando listagem de denúncias com filtros: status={}, categoria={}, search={}",
        status, categoria, search);

    // Passo 1: Buscar apenas os IDs com paginação e ordenação personalizada
    Page<Long> idsPage;

    if (search != null && !search.trim().isEmpty()) {
      String termo = search.trim();
      
      // Tenta primeiro como UUID (busca exata por token)
      try {
        UUID token = UUID.fromString(termo);
        String tokenSearch = token.toString();
        log.debug("Busca por token UUID: {}", maskToken(token));
        
        idsPage = denunciaRepository.findAllIdsWithFiltersOrderedByNewMessages(
            status, categoria, tokenSearch, pageable);
            
      } catch (IllegalArgumentException e) {
        // Não é UUID - verifica se tem pelo menos 3 caracteres para busca textual
        if (termo.length() >= 3) {
          log.info("Busca textual por termo: '{}' (caracteres: {})", termo, termo.length());
          
          // Usa a função SQL otimizada para buscar IDs das denúncias
          List<Long> idsEncontrados = denunciaRepository.buscarIdsPorTexto(termo);
          
          if (idsEncontrados.isEmpty()) {
            // Nenhum resultado encontrado - retorna página vazia
            log.info("Nenhuma denúncia encontrada com o termo de busca: '{}'", termo);
            return Page.empty(pageable);
          }
          
          log.info("Busca textual encontrou {} denúncias com o termo '{}'", idsEncontrados.size(), termo);
          
          // Buscar denúncias completas com filtros de status e categoria
          // Este método já aplica os filtros e ordenação (mensagens não lidas + data)
          List<Denuncia> denunciasFiltradas = denunciaRepository.findByIdsWithFiltersOrdered(
              idsEncontrados, status, categoria);
          
          if (denunciasFiltradas.isEmpty()) {
            log.info("Nenhuma denúncia encontrada após aplicar filtros (status={}, categoria={})", 
                status, categoria);
            return Page.empty(pageable);
          }
          
          log.info("Após filtros: {} denúncias (de {} encontradas pela busca)", 
              denunciasFiltradas.size(), idsEncontrados.size());
          
          // Extrair apenas os IDs ordenados
          List<Long> idsOrdenados = denunciasFiltradas.stream()
              .map(Denuncia::getId)
              .collect(Collectors.toList());
          
          // Aplicar paginação MANUALMENTE sobre os IDs ordenados e filtrados
          int pageSize = pageable.getPageSize();
          int currentPage = pageable.getPageNumber();
          int startItem = currentPage * pageSize;
          int totalElements = idsOrdenados.size();
          
          // Verificar se a página solicitada está dentro dos limites
          if (startItem >= totalElements) {
            log.info("Página {} está fora dos limites (total: {} elementos)", currentPage, totalElements);
            return Page.empty(pageable);
          }
          
          // Calcular índice final (não pode ultrapassar o tamanho da lista)
          int endItem = Math.min(startItem + pageSize, totalElements);
          
          // Extrair apenas os IDs da página atual
          List<Long> idsPaginados = idsOrdenados.subList(startItem, endItem);
          
          log.info("Página {}: retornando {} IDs (de {} até {}) de um total de {} após filtros", 
              currentPage, idsPaginados.size(), startItem, endItem - 1, totalElements);
          
          // Buscar as denúncias da página (já estão na lista, só precisamos filtrar)
          List<Denuncia> denunciasPagina = denunciasFiltradas.stream()
              .filter(d -> idsPaginados.contains(d.getId()))
              .sorted(Comparator.comparingInt(d -> idsPaginados.indexOf(d.getId())))
              .collect(Collectors.toList());
          
          // Converter para DTO
          List<DenunciaAdminResponseDto> dtos = denunciasPagina.stream()
              .map(this::mapToDenunciaAdminResponseDto)
              .collect(Collectors.toList());
          
          // Retornar Page com o total correto de elementos
          return new PageImpl<>(dtos, pageable, totalElements);
          
        } else {
          log.warn("Termo de busca muito curto (< 3 caracteres): '{}' - retornando vazio", termo);
          return Page.empty(pageable);
        }
      }
    } else {
      // Sem busca por token
      idsPage = denunciaRepository.findAllIdsWithFiltersOrderedByNewMessages(
          status, categoria, null, pageable);
    }

    // Se não encontrou nada, retorna página vazia
    if (idsPage.isEmpty()) {
      log.info("Nenhuma denúncia encontrada com os filtros aplicados");
      return Page.empty(pageable);
    }

    log.debug("Encontrados {} IDs de denúncias", idsPage.getContent().size());

    // Passo 2: Buscar as entidades completas com relacionamentos (1 query com JOINs)
    List<Denuncia> denuncias = denunciaRepository.findAllByIdWithRelations(idsPage.getContent());

    log.debug("Carregadas {} denúncias com relacionamentos", denuncias.size());

    // Passo 3: Ordenar a lista na mesma ordem dos IDs retornados
    List<Long> idsOrder = idsPage.getContent();
    Map<Long, Integer> orderMap = IntStream.range(0, idsOrder.size())
        .boxed()
        .collect(Collectors.toMap(idsOrder::get, i -> i));

    denuncias.sort(Comparator.comparingInt(d -> orderMap.getOrDefault(d.getId(), Integer.MAX_VALUE)));

    // Passo 4: Converter para DTO
    List<DenunciaAdminResponseDto> dtos = denuncias.stream()
        .map(this::mapToDenunciaAdminResponseDto)
        .collect(Collectors.toList());

    log.info("Retornando {} denúncias para a página {}", dtos.size(), pageable.getPageNumber());

    // Retornar Page com os dados originais de paginação
    return new PageImpl<>(dtos, pageable, idsPage.getTotalElements());
  }

  // buscar denúncia por ID
  @Transactional
  public DenunciaAdminResponseDto buscarPorId(Long id) {
    Denuncia denuncia = denunciaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada com o ID: " + id));

    // Marcar mensagens do ANONIMO (usuário) como visualizadas quando admin acessa
    acompanhamentoRepository.marcarComoVisualizadoPorDenunciaEAutor(id, Perfis.ANONIMO);

    return mapToDenunciaAdminResponseDto(denuncia);
  }

  public Optional<DenunciaAdminResponseDto> atualizarDenuncia(Long id, AtualizarDenunciaDto dto,
      String adminName) {
    log.info("Iniciando atualização da denúncia id {} por admin {}.", id, adminName);
    return denunciaRepository.findById(id).map(denuncia -> {
      if (denuncia.getStatus() == Status.RESOLVIDO || denuncia.getStatus() == Status.REJEITADO) {
        log.warn("Tentativa de atualização de denúncia id {} em estado final.", id);
        throw new IllegalStateException(
            "Denúncia já está em estado final e não pode ser alterada.");
      }

      denuncia.setStatus(dto.status());
      denuncia.setAlteradoEm(LocalDateTime.now());
      denuncia.setAlteradoPor(adminName);

      // sanitizar motivoRejeicao
      if (dto.motivoRejeicao() != null) {
        String motivoRejeicaoSanitizado = policy.sanitize(dto.motivoRejeicao());
        denuncia.setMotivoRejeicao(motivoRejeicaoSanitizado);
      } else {
        denuncia.setMotivoRejeicao(null);
      }

      Denuncia denunciaAtualizada = denunciaRepository.save(denuncia);
      log.info("Denúncia id {} atualizada com sucesso para o status {}.", id, dto.status());
      return mapToDenunciaAdminResponseDto(denunciaAtualizada);
    });
  }

  public boolean deletarDenuncia(Long id) {
    Optional<Denuncia> denuncia = denunciaRepository.findById(id);
    if (denuncia.isPresent()) {
      log.info("Iniciando deleção da denúncia id {}.", id);
      denunciaRepository.delete(denuncia.get());
      log.info("Denúncia id {} deletada com sucesso.", id);
      return true;
    }
    log.warn("Tentativa de deleção de denúncia id {} que não existe.", id);
    return false;
  }

  @Transactional(readOnly = true)
  public List<AcompanhamentoDto> listarAcompanhamentosPorToken(UUID tokenAcompanhamento) {
    log.info("Listando acompanhamentos (público) para o token: {}", maskToken(tokenAcompanhamento));
    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento).orElseThrow(
        () -> new EntityNotFoundException("Denúncia não encontrada com o token informado."));

    return denuncia.getAcompanhamentos().stream().map(this::mapToAcompanhamentoResponseDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<AcompanhamentoDto> listarAcompanhamentosPorId(Long id) {
    log.info("Listando acompanhamentos (admin) para a denúncia ID: {}", id);
    Denuncia denuncia = denunciaRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("Denúncia não encontrada com o ID informado."));

    return denuncia.getAcompanhamentos().stream().map(this::mapToAcompanhamentoResponseDto)
        .collect(Collectors.toList());
  }

  public AcompanhamentoDto adicionarAcompanhamentoDenunciante(UUID tokenAcompanhamento,
      AcompanhamentoDto dto) {
    log.info("Adicionando acompanhamento (público) para o token: {}",
        maskToken(tokenAcompanhamento));

    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(d -> d.getStatus() != Status.RESOLVIDO && d.getStatus() != Status.REJEITADO)
        .orElseThrow(() -> new EntityNotFoundException(
            "Denúncia não encontrada, finalizada ou token inválido."));

    // verificar se a última mensagem foi do admin (anti-flood)
    Optional<Acompanhamento> ultimoOpt = acompanhamentoRepository
        .findTopByDenuncia_TokenAcompanhamentoOrderByDataEnvioDesc(tokenAcompanhamento);

    if (ultimoOpt.isPresent()) {
      Acompanhamento ultimo = ultimoOpt.get();
      if (ultimo.getAutor() == Perfis.ANONIMO) {
        log.warn("Bloqueio de flood no token: {}", maskToken(tokenAcompanhamento));
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Você precisa aguardar a resposta do administrador antes de enviar outra mensagem.");
      }
    }

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    novoAcompanhamento.setMensagem(policy.sanitize(dto.mensagem()));
    novoAcompanhamento.setDenuncia(denuncia);

    // Define o perfil do autor como ANONIMO (denunciantes são sempre anônimos no
    // acompanhamento
    // público)
    novoAcompanhamento.setAutor(Perfis.ANONIMO);

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    log.info("Acompanhamento adicionado com sucesso a denúncia de token: {}",
        maskToken(tokenAcompanhamento));
    // Disparar notificação externa para informar que uma nova mensagem do
    // denunciante
    // foi recebida para a denúncia. Envolve apenas mensagens enviadas pelo
    // denunciante
    // (perfil ANONIMO) — trata-se do fluxo público.
    try {
      notificacaoExternaService.notificarNovaMensagem(salvo);
      log.info("Notificação externa enviada para nova mensagem (denúncia ID {}).",
          salvo.getDenuncia().getId());
    } catch (Exception e) {
      log.error("Erro ao enviar notificação externa para nova mensagem (denúncia ID {}): {}",
          salvo.getDenuncia().getId(), e.getMessage(), e);
    }

    return mapToAcompanhamentoResponseDto(salvo);

  }

  public AcompanhamentoDto adicionarAcompanhamentoAdmin(Long id, AcompanhamentoDto dto,
      String nomeAdmin) {
    log.info("Adicionando acompanhamento (admin) para a denúncia ID: {}", id);
    Denuncia denuncia = denunciaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada."));

    if (denuncia.getStatus() == Status.RESOLVIDO || denuncia.getStatus() == Status.REJEITADO) {
      log.warn("Tentativa de adicionar acompanhamento a denúncia finalizada (ID: {})", id);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN,
          "Não é possível adicionar mensagens a denúncias com status Resolvido ou Rejeitado.");
    }

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    novoAcompanhamento.setMensagem(policy.sanitize(dto.mensagem()));
    novoAcompanhamento.setDenuncia(denuncia);
    novoAcompanhamento.setAutor(Perfis.ADMIN);

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    log.info("Acompanhamento adicionado com sucesso à denúncia ID: {}", id);
    return mapToAcompanhamentoResponseDto(salvo);
  }

  public DenunciaAdminResponseDto alterarStatus(Long id, Status novoStatus, String adminName) {
    log.info("Iniciando alteração de status da denúncia ID {} para {} por admin {}.", id,
        novoStatus, adminName);

    Denuncia denuncia = denunciaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada."));

    if (denuncia.getStatus() == Status.RESOLVIDO || denuncia.getStatus() == Status.REJEITADO) {
      log.warn("Tentativa de alteração de status de denúncia ID {} em estado final.", id);
      throw new IllegalStateException("Denúncia já está em estado final e não pode ser alterada.");
    }

    Status statusAnterior = denuncia.getStatus();
    denuncia.setStatus(novoStatus);
    denuncia.setAlteradoEm(LocalDateTime.now());
    denuncia.setAlteradoPor(adminName);

    Denuncia denunciaAtualizada = denunciaRepository.save(denuncia);

    // Criar mensagem automática de mudança de status
    String mensagemStatus = gerarMensagemMudancaStatus(statusAnterior, novoStatus);
    Acompanhamento acompanhamentoStatus = new Acompanhamento();
    acompanhamentoStatus.setMensagem(mensagemStatus);
    acompanhamentoStatus.setDenuncia(denunciaAtualizada);
    acompanhamentoStatus.setAutor(Perfis.ADMIN);
    acompanhamentoRepository.save(acompanhamentoStatus);

    log.info(
        "Status da denúncia ID {} alterado de {} para {} com mensagem automática de acompanhamento.",
        id, statusAnterior, novoStatus);

    return mapToDenunciaAdminResponseDto(denunciaAtualizada);
  }

  private String gerarMensagemMudancaStatus(Status statusAnterior, Status novoStatus) {
    String statusAnteriorFormatado = formatarStatusParaMensagem(statusAnterior);
    String novoStatusFormatado = formatarStatusParaMensagem(novoStatus);

    return String.format(
        "O status da sua denúncia foi alterado de '%s' para '%s'. " + "Acompanhe as atualizações.",
        statusAnteriorFormatado, novoStatusFormatado);
  }

  private String formatarStatusParaMensagem(Status status) {
    return switch (status) {
      case RECEBIDO -> "Recebido";
      case EM_ANALISE -> "Em Análise";
      case AGUARDANDO -> "Aguardando Informações";
      case RESOLVIDO -> "Resolvido";
      case REJEITADO -> "Rejeitado";
    };
  }

  private DenunciaResponseDto mapToDenunciaResponseDto(Denuncia denuncia) {
    // Verifica se há mensagens não lidas do ADMIN para o ANONIMO (usuário)
    boolean temMensagemNaoLida = acompanhamentoRepository
        .existsByDenunciaIdAndAutorAndVisualizadoFalse(denuncia.getId(), Perfis.ADMIN);

    // Mapear dados do denunciante, se existir e se deseja se identificar
    DenuncianteResponseDto denuncianteDto = null;
    if (denuncia.isDesejaSeIdentificar() && denuncia.getDenunciante() != null) {
      Denunciante denunciante = denuncia.getDenunciante();
      denuncianteDto =
          new DenuncianteResponseDto(denunciante.getNomeCompleto(), denunciante.getGrau(),
              denunciante.getCurso(), denunciante.getAno(), denunciante.getTurma());
    }

    return new DenunciaResponseDto(denuncia.getId(), denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(), denuncia.getCategoria(), denuncia.getCriadoEm(),
        denuncia.getAlteradoEm(), temMensagemNaoLida, denuncianteDto);
  }

  /**
   * Mapeia uma entidade Denuncia para DenunciaAdminResponseDto.
   *
   * @param denuncia entidade a ser mapeada
   * @return DTO mapeado
   */
  private DenunciaAdminResponseDto mapToDenunciaAdminResponseDto(Denuncia denuncia) {
    // Verificar se tem mensagens não lidas do ANONIMO (usuário/denunciante)
    boolean temMensagemNaoLida = denuncia.getAcompanhamentos().stream()
        .anyMatch(a -> a.getAutor() == Perfis.ANONIMO && !a.getVisualizado());

    // Mapear dados do denunciante, se existir e se deseja se identificar
    DenuncianteResponseDto denuncianteDto = null;
    if (denuncia.isDesejaSeIdentificar() && denuncia.getDenunciante() != null) {
      Denunciante denunciante = denuncia.getDenunciante();
      denuncianteDto =
          new DenuncianteResponseDto(denunciante.getNomeCompleto(), denunciante.getGrau(),
              denunciante.getCurso(), denunciante.getAno(), denunciante.getTurma());
    }

    return new DenunciaAdminResponseDto(
        denuncia.getId(),
        denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(),
        denuncia.getCategoria(),
        denuncia.getCriadoEm(),
        denuncia.getAlteradoEm(),
        temMensagemNaoLida,
        denuncianteDto
    );
  }

  private AcompanhamentoDto mapToAcompanhamentoResponseDto(Acompanhamento acompanhamento) {
    return new AcompanhamentoDto(acompanhamento.getId(), acompanhamento.getMensagem(),
        acompanhamento.getAutor().getDisplayName(), acompanhamento.getDataEnvio());
  }

  /**
   * Mascara um token UUID mostrando apenas os primeiros 8 caracteres seguidos de
   * "...***". Previne
   * exposição completa de tokens sensíveis nos logs.
   * 
   * @param token o token UUID a ser mascarado
   * @return token mascarado ou indicação de null
   */
  private static String maskToken(UUID token) {
    if (token == null) {
      return "null";
    }
    String tokenStr = token.toString();
    if (tokenStr.length() <= 8) {
      return "***";
    }
    return tokenStr.substring(0, 8) + "...***";
  }

}
