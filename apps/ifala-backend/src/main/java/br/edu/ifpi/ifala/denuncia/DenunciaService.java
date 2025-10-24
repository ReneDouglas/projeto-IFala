package br.edu.ifpi.ifala.denuncia;

import org.springframework.stereotype.Service;
import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.acompanhamento.AcompanhamentoRepository;
import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.*;
import br.edu.ifpi.ifala.shared.enums.Categorias;
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

/**
 * Classe de serviço responsável por manipular operações relacionadas a
 * denúncias.
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
      AcompanhamentoRepository acompanhamentoRepository) {
    this.denunciaRepository = denunciaRepository;
    this.acompanhamentoRepository = acompanhamentoRepository;
    this.policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
  }

  public DenunciaResponseDto criarDenuncia(CriarDenunciaDto dto) {

    // Validação do reCAPTCHA - A SER USADO DEPOIS QUE O RECAPTCHA ESTIVER
    // FUNCIONANDO EM PRODUÇÃO

    // boolean isRecaptchaValid =
    // recaptchaService.validarToken(dto.getRecaptchaToken()).block();

    // if (!isRecaptchaValid) {
    // throw new RuntimeException("Falha na validação do ReCaptcha.");
    // }

    Denuncia novaDenuncia = new Denuncia();
    novaDenuncia.setDescricao(policy.sanitize(dto.descricao()));

    novaDenuncia.setCategoria(dto.categoria());

    if (Boolean.TRUE.equals(dto.desejaSeIdentificar()) && dto.dadosDeIdentificacao() != null) {

      log.info("Processando criação de denúncia identificada.");
      novaDenuncia.setDesejaSeIdentificar(true);

      Denunciante denunciante = new Denunciante();
      DadosDeIdentificacaoDto idDto = dto.dadosDeIdentificacao();

      denunciante.setNomeCompleto(policy.sanitize(idDto.nomeCompleto()));
      denunciante.setEmail(policy.sanitize(idDto.email()));
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

    return mapToDenunciaResponseDto(denunciaSalva);
  }

  @Transactional(readOnly = true)
  public Optional<DenunciaResponseDto> consultarPorTokenAcompanhamento(UUID tokenAcompanhamento) {
    return denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(denuncia -> denuncia.getStatus() != Status.RESOLVIDO
            && denuncia.getStatus() != Status.REJEITADO)
        .map(this::mapToDenunciaResponseDto);
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

  public AcompanhamentoDto adicionarAcompanhamentoDenunciante(UUID tokenAcompanhamento, AcompanhamentoDto dto) {
    log.info("Adicionando acompanhamento (público) para o token: {}", tokenAcompanhamento);
    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(d -> d.getStatus() != Status.RESOLVIDO && d.getStatus() != Status.REJEITADO)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada, finalizada ou token inválido."));

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    novoAcompanhamento.setMensagem(policy.sanitize(dto.mensagem()));
    novoAcompanhamento.setDenuncia(denuncia);

    // se o denunciante optou por se identificar, usar o nome completo dele como
    // autor
    if (denuncia.isDesejaSeIdentificar() && denuncia.getDenunciante() != null
        && denuncia.getDenunciante().getNomeCompleto() != null
        && !denuncia.getDenunciante().getNomeCompleto().isBlank()) {
      novoAcompanhamento.setAutor(denuncia.getDenunciante().getNomeCompleto());
    } else {
      novoAcompanhamento.setAutor("DENUNCIANTE"); // se for anônimo, usar "DENUNCIANTE"
    }

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
    novoAcompanhamento.setAutor(policy.sanitize(nomeAdmin));

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    log.info("Acompanhamento adicionado com sucesso à denúncia ID: {}", id);
    return mapToAcompanhamentoResponseDto(salvo);
  }

  private DenunciaResponseDto mapToDenunciaResponseDto(Denuncia denuncia) {
    return new DenunciaResponseDto(
        denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(),
        denuncia.getCategoria(),
        denuncia.getCriadoEm());
  }

  private DenunciaAdminResponseDto mapToDenunciaAdminResponseDto(Denuncia denuncia) {
    return new DenunciaAdminResponseDto(
        denuncia.getId(),
        denuncia.getTokenAcompanhamento(),
        denuncia.getStatus(),
        denuncia.getCategoria(),
        denuncia.getCriadoEm());
  }

  private AcompanhamentoDto mapToAcompanhamentoResponseDto(Acompanhamento acompanhamento) {
    return new AcompanhamentoDto(
        acompanhamento.getMensagem(),
        acompanhamento.getAutor(),
        acompanhamento.getDataEnvio());
  }

}
