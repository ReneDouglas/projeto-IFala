package br.edu.ifpi.ifala.denuncia;

import org.springframework.stereotype.Service;
import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.acompanhamento.AcompanhamentoRepository;
import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.AtualizarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.CriarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
  private final RecaptchaService recaptchaService;

  public DenunciaService(DenunciaRepository denunciaRepository,
      AcompanhamentoRepository acompanhamentoRepository, RecaptchaService recaptchaService) {
    this.denunciaRepository = denunciaRepository;
    this.acompanhamentoRepository = acompanhamentoRepository;
    this.recaptchaService = recaptchaService;
  }

  public DenunciaResponseDto criarDenuncia(CriarDenunciaDto dto) {

    boolean isRecaptchaValid = recaptchaService.validarToken(dto.getRecaptchaToken(), "denuncia", 0.5);

    if (!isRecaptchaValid) {

      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Falha na validação do ReCaptcha.");
    }

    Denuncia novaDenuncia = new Denuncia();
    novaDenuncia.setDescricao(dto.getDescricao());
    novaDenuncia.setCategoria(dto.getCategoria());

    PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    String descricaoSanitizada = policy.sanitize(novaDenuncia.getDescricao());
    novaDenuncia.setDescricao(descricaoSanitizada);

    Denuncia denunciaSalva = denunciaRepository.save(novaDenuncia);
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
  public Page<DenunciaResponseDto> listarTodas(Status status, Categorias categoria,
      Pageable pageable) {
    Specification<Denuncia> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (categoria != null) {
        predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
      }
      query.distinct(true);

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    return denunciaRepository.findAll(spec, pageable).map(this::mapToDenunciaResponseDto);
  }

  public Optional<DenunciaResponseDto> atualizarDenuncia(Long id, AtualizarDenunciaDto dto,
      String adminName) {
    return denunciaRepository.findById(id).map(denuncia -> {
      if (denuncia.getStatus() == Status.RESOLVIDO || denuncia.getStatus() == Status.REJEITADO) {
        throw new IllegalStateException(
            "Denúncia já está em estado final e não pode ser alterada.");
      }

      denuncia.setStatus(dto.getStatus());
      denuncia.setMotivoRejeicao(dto.getMotivoRejeicao());
      denuncia.setAlteradoEm(LocalDateTime.now());
      denuncia.setAlteradoPor(adminName);

      PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
      String motivoRejeicaoSanitizado = policy.sanitize(dto.getMotivoRejeicao());
      denuncia.setMotivoRejeicao(motivoRejeicaoSanitizado);

      Denuncia denunciaAtualizada = denunciaRepository.save(denuncia);

      return mapToDenunciaResponseDto(denunciaAtualizada);
    });
  }

  public boolean deletarDenuncia(Long id) {
    Optional<Denuncia> denuncia = denunciaRepository.findById(id);
    if (denuncia.isPresent()) {
      denunciaRepository.delete(denuncia.get());
      return true;
    }
    return false;
  }

  @Transactional(readOnly = true)
  public List<AcompanhamentoDto> listarAcompanhamentosPorToken(UUID tokenAcompanhamento) {
    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento).orElseThrow(
        () -> new EntityNotFoundException("Denúncia não encontrada com o token informado."));

    return denuncia.getAcompanhamentos().stream().map(this::mapToAcompanhamentoResponseDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<AcompanhamentoDto> listarAcompanhamentosPorId(Long id) {
    Denuncia denuncia = denunciaRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("Denúncia não encontrada com o ID informado."));

    return denuncia.getAcompanhamentos().stream().map(this::mapToAcompanhamentoResponseDto)
        .collect(Collectors.toList());
  }

  public AcompanhamentoDto adicionarAcompanhamentoDenunciante(UUID tokenAcompanhamento,
      AcompanhamentoDto dto) {
    Denuncia denuncia = denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(d -> d.getStatus() != Status.RESOLVIDO && d.getStatus() != Status.REJEITADO)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada ou finalizada."));

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    String mensagemSanitizada = policy.sanitize(dto.getMensagem());
    novoAcompanhamento.setMensagem(mensagemSanitizada);
    novoAcompanhamento.setAutor("DENUNCIANTE");
    novoAcompanhamento.setDenuncia(denuncia);

    String autorSanitizado = policy.sanitize(dto.getAutor());
    novoAcompanhamento.setAutor(autorSanitizado);

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    return mapToAcompanhamentoResponseDto(salvo);
  }

  public AcompanhamentoDto adicionarAcompanhamentoAdmin(Long id, AcompanhamentoDto dto,
      String nomeAdmin) {
    Denuncia denuncia = denunciaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Denúncia não encontrada."));

    Acompanhamento novoAcompanhamento = new Acompanhamento();
    PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
    String mensagemSanitizada = policy.sanitize(dto.getMensagem());
    novoAcompanhamento.setMensagem(mensagemSanitizada);
    novoAcompanhamento.setAutor(nomeAdmin);
    novoAcompanhamento.setDenuncia(denuncia);

    String autorSanitizado = policy.sanitize(nomeAdmin);
    novoAcompanhamento.setAutor(autorSanitizado);

    Acompanhamento salvo = acompanhamentoRepository.save(novoAcompanhamento);
    return mapToAcompanhamentoResponseDto(salvo);
  }

  private DenunciaResponseDto mapToDenunciaResponseDto(Denuncia denuncia) {
    DenunciaResponseDto dto = new DenunciaResponseDto();

    dto.setTokenAcompanhamento(denuncia.getTokenAcompanhamento());
    dto.setStatus(denuncia.getStatus());
    dto.setCategoria(denuncia.getCategoria());
    dto.setCriadoEm(denuncia.getCriadoEm());

    return dto;
  }

  private AcompanhamentoDto mapToAcompanhamentoResponseDto(Acompanhamento acompanhamento) {
    AcompanhamentoDto dto = new AcompanhamentoDto();
    dto.setAutor(acompanhamento.getAutor());
    dto.setMensagem(acompanhamento.getMensagem());
    dto.setDataEnvio(acompanhamento.getDataEnvio());
    return dto;
  }
}
