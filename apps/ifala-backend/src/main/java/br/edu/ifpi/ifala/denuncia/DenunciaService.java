package br.edu.ifpi.ifala.denuncia;

import org.springframework.stereotype.Service;
import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.acompanhamento.AcompanhamentoRepository;
import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.*;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Perfis;
import br.edu.ifpi.ifala.shared.enums.Status;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.edu.ifpi.ifala.security.recaptcha.RecaptchaService;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/**
 * Classe de serviço responsável por manipular operações relacionadas a denúncias.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */

@Service
@Transactional
public class DenunciaService {

  private static final Logger log = LoggerFactory.getLogger(DenunciaService.class);

  private final DenunciaRepository denunciaRepository;
  private final AcompanhamentoRepository acompanhamentoRepository;
  private final RecaptchaService recaptchaService;
  private final PolicyFactory policy;

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
      AcompanhamentoRepository acompanhamentoRepository, RecaptchaService recaptchaService) {
    this.denunciaRepository = denunciaRepository;
    this.acompanhamentoRepository = acompanhamentoRepository;
    this.recaptchaService = recaptchaService;
    this.policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
  }

  public DenunciaResponseDto criarDenuncia(CriarDenunciaDto dto) {

    log.info("Iniciando validação do reCAPTCHA para nova denúncia.");

    boolean isRecaptchaValid = recaptchaService.validarToken(dto.recaptchaToken(), "denuncia", 0.5);

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
    log.info("Token de acompanhamento gerado: {}", denunciaSalva.getTokenAcompanhamento());

    // Criar automaticamente o primeiro acompanhamento com o relato da denúncia
    Acompanhamento primeiroAcompanhamento = new Acompanhamento();
    primeiroAcompanhamento.setMensagem(denunciaSalva.getDescricao());
    primeiroAcompanhamento.setDenuncia(denunciaSalva);
    primeiroAcompanhamento.setAutor(Perfis.ANONIMO);
    acompanhamentoRepository.save(primeiroAcompanhamento);
    log.info("Primeiro acompanhamento criado automaticamente com o relato da denúncia.");

    return mapToDenunciaResponseDto(denunciaSalva);
  }

  @Transactional(readOnly = true)
  public Optional<DenunciaResponseDto> consultarPorTokenAcompanhamento(UUID tokenAcompanhamento) {
    return denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .map(this::mapToDenunciaResponseDto);
  }

  /*
   * tipo Page é uma interface do Spring Data que encapsula uma página de dados Pageable é uma
   * interface que define a paginação e ordenação Specification é uma interface do Spring Data JPA
   * que permite construir consultas dinamicamente predicate é uma condição usada em consultas para
   * filtrar resultados
   */

  @Transactional(readOnly = true) // apenas leitura
  public Page<DenunciaAdminResponseDto> listarTodas(Status status, Categorias categoria,
      Pageable pageable) {
    Specification<Denuncia> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (categoria != null) {
        predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
      }

      if (query != null) {
        query.distinct(true);
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    return denunciaRepository.findAll(spec, pageable).map(this::mapToDenunciaAdminResponseDto);
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
    log.info("Listando acompanhamentos (público) para o token: {}", tokenAcompanhamento);
    Denuncia denuncia =
        denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento).orElseThrow(
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
    log.info("Adicionando acompanhamento (público) para o token: {}", tokenAcompanhamento);
    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(d -> d.getStatus() != Status.RESOLVIDO && d.getStatus() != Status.REJEITADO)
        .orElseThrow(() -> new EntityNotFoundException(
            "Denúncia não encontrada, finalizada ou token inválido."));

    // Verificar se a última mensagem foi do admin (anti-flood)
    List<Acompanhamento> acompanhamentos = new ArrayList<>(denuncia.getAcompanhamentos());
    if (!acompanhamentos.isEmpty()) {
      Acompanhamento ultimaMensagem = acompanhamentos.get(acompanhamentos.size() - 1);
      if (ultimaMensagem.getAutor() == Perfis.ANONIMO) {
        log.warn("Tentativa de envio de mensagem consecutiva pelo denunciante no token: {}",
            tokenAcompanhamento);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
            "Você precisa aguardar a resposta do administrador antes de enviar outra mensagem.");
      }
    }

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    novoAcompanhamento.setMensagem(policy.sanitize(dto.mensagem()));
    novoAcompanhamento.setDenuncia(denuncia);

    // Define o perfil do autor como ANONIMO (denunciantes são sempre anônimos no acompanhamento
    // público)
    novoAcompanhamento.setAutor(Perfis.ANONIMO);

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    log.info("Acompanhamento adicionado com sucesso a denúncia de token: {}", tokenAcompanhamento);
    return mapToAcompanhamentoResponseDto(salvo);

  }

  public AcompanhamentoDto adicionarAcompanhamentoAdmin(Long id, AcompanhamentoDto dto,
      String nomeAdmin) {
    log.info("Adicionando acompanhamento (admin) para a denúncia ID: {}", id);
    Denuncia denuncia = denunciaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada."));

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
    return new DenunciaResponseDto(denuncia.getId(), denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(), denuncia.getCategoria(), denuncia.getCriadoEm());
  }

  private DenunciaAdminResponseDto mapToDenunciaAdminResponseDto(Denuncia denuncia) {
    return new DenunciaAdminResponseDto(denuncia.getId(), denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(), denuncia.getCategoria(), denuncia.getCriadoEm());
  }

  private AcompanhamentoDto mapToAcompanhamentoResponseDto(Acompanhamento acompanhamento) {
    return new AcompanhamentoDto(acompanhamento.getId(), acompanhamento.getMensagem(),
        acompanhamento.getAutor().getDisplayName(), acompanhamento.getDataEnvio());
  }

}
