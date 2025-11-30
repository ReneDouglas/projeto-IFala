package br.edu.ifpi.ifala.denuncia;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDTO;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.CriarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsável pelos endpoints PÚBLICOS de denúncias. Permite a criação e consulta de
 * denúncias por token.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@RestController
@RequestMapping("/api/v1/public/denuncias")
@Tag(name = "Denúncias Públicas",
    description = "Endpoints públicos para criar e consultar o andamento de denúncias.")
public class DenunciaPublicController {

  private static final Logger log = LoggerFactory.getLogger(DenunciaPublicController.class);
  private final DenunciaService denunciaService;

  public DenunciaPublicController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  @PostMapping
  @Operation(summary = "Cria uma nova denúncia anônima",
      description = "Registra uma nova denúncia no sistema e retorna um token único para acompanhamento.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Denúncia criada com sucesso",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = DenunciaResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Dados da denúncia inválidos",
          content = @Content)})
  public ResponseEntity<DenunciaResponseDto> criarDenuncia(
      @Valid @RequestBody CriarDenunciaDto novaDenunciaDto, UriComponentsBuilder uriBuilder) {

    log.info("Recebida requisição para criar nova denúncia pública.");
    try {
      DenunciaResponseDto denunciaSalvaDto = denunciaService.criarDenuncia(novaDenunciaDto);
      URI uri = uriBuilder.path("/api/v1/public/denuncias/{token}")
          .buildAndExpand(denunciaSalvaDto.tokenAcompanhamento()).toUri();

      log.info("Denúncia criada com sucesso. Token de acompanhamento: {}",
          denunciaSalvaDto.tokenAcompanhamento());

      return ResponseEntity.created(uri).body(denunciaSalvaDto);
    } catch (Exception e) {
      log.error("Erro ao criar denúncia: {}", e.getMessage());
      throw e; // lança a exceção para ser tratada globalmente
    }

  }

  @GetMapping("/{tokenAcompanhamento}")
  @Operation(summary = "Consulta uma denúncia pelo token",
      description = "Retorna os detalhes de uma denúncia específica usando o token de acompanhamento fornecido na criação.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Denúncia encontrada",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = DenunciaResponseDto.class))),
      @ApiResponse(responseCode = "404",
          description = "Denúncia não encontrada para o token fornecido", content = @Content)})
  public ResponseEntity<DenunciaResponseDto> consultarPorToken(@Parameter(
      description = "Token de acompanhamento da denúncia", required = true,
      example = "123e4567-e89b-12d3-a456-426614174000") @PathVariable UUID tokenAcompanhamento) {
    log.info("Recebida requisição de consulta pública por token: {}", tokenAcompanhamento);
    return denunciaService.consultarPorTokenAcompanhamento(tokenAcompanhamento)
        .map(ResponseEntity::ok).orElseGet(() -> {
          log.warn("Denúncia não encontrada (público) para o token: {}", tokenAcompanhamento);
          return ResponseEntity.notFound().build();
        });
  }

  @GetMapping("/{tokenAcompanhamento}/acompanhamentos")
  @Operation(summary = "Lista os acompanhamentos de uma denúncia pelo token",
      description = "Retorna todo o histórico de acompanhamentos de uma denúncia usando o token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Acompanhamentos listados com sucesso"),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})
  public ResponseEntity<List<AcompanhamentoDTO>> listarAcompanhamentos(
      @Parameter(description = "Token de acompanhamento da denúncia",
          required = true) @PathVariable UUID tokenAcompanhamento) {
    log.info("Recebida requisição para listar acompanhamentos (público) por token: {}",
        tokenAcompanhamento);
    try {
      List<AcompanhamentoDTO> acompanhamentos =
          denunciaService.listarAcompanhamentosPorToken(tokenAcompanhamento);
      return ResponseEntity.ok(acompanhamentos);
    } catch (EntityNotFoundException e) {
      log.warn("Denúncia não encontrada (público) ao listar acompanhamentos por token: {}",
          tokenAcompanhamento);
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/{tokenAcompanhamento}/acompanhamentos")
  @Operation(summary = "Adiciona um novo acompanhamento (pelo denunciante)",
      description = "Permite que o denunciante original adicione uma nova mensagem ou atualização ao histórico da sua denúncia usando o token.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Acompanhamento adicionado com sucesso",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AcompanhamentoDTO.class))),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})

  public ResponseEntity<AcompanhamentoDTO> adicionarAcompanhamento(
      @Parameter(description = "Token de acompanhamento da denúncia",
          required = true) @PathVariable UUID tokenAcompanhamento,
      @Valid @RequestBody AcompanhamentoDTO novoAcompanhamento) {
    log.info("Recebida requisição para adicionar acompanhamento (público) por token: {}",
        tokenAcompanhamento);
    try {
      AcompanhamentoDTO acompanhamentoSalvo = denunciaService
          .adicionarAcompanhamentoDenunciante(tokenAcompanhamento, novoAcompanhamento);
      return ResponseEntity.status(HttpStatus.CREATED).body(acompanhamentoSalvo);
    } catch (EntityNotFoundException e) {
      log.warn(
          "Denúncia não encontrada ou finalizada ao adicionar acompanhamento (público) por token: {}",
          tokenAcompanhamento);
      return ResponseEntity.notFound().build();
    }
  }

}
