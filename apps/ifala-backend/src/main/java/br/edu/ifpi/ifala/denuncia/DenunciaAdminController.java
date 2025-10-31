package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.AtualizarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaAdminResponseDto;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Controller responsável pelos endpoints de ADMINISTRAÇÃO de denúncias. Requer
 * autenticação e
 * autorização.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@RestController
@RequestMapping("/api/v1/admin/denuncias")
@Tag(name = "Administração de Denúncias", description = "Endpoints restritos para administradores gerenciarem denúncias.")
@SecurityRequirement(name = "bearerAuth")
public class DenunciaAdminController {

  private static final Logger log = LoggerFactory.getLogger(DenunciaAdminController.class);
  private final DenunciaService denunciaService;

  /**
   * Construtor do controller de administração de denúncias.
   *
   * @param denunciaService o serviço de denúncias
   */
  public DenunciaAdminController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  /**
   * Lista todas as denúncias com filtros e paginação.
   *
   * @param status        filtro por status (opcional)
   * @param categoria     filtro por categoria (opcional)
   * @param pageNumber    número da página (base 0)
   * @param size          tamanho da página
   * @param sortDirection direção da ordenação (ASC ou DESC)
   * @return página de denúncias
   */

  @GetMapping
  @Operation(summary = "Lista todas as denúncias com filtros e paginação", description = "Retorna lista paginada de denúncias para administração.")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
      @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content) })
  public ResponseEntity<Page<DenunciaAdminResponseDto>> listarTodas(
      @Parameter(description = "Filtrar por status") @RequestParam(required = false) Status status,
      @Parameter(description = "Filtrar por categoria") @RequestParam(required = false) Categorias categoria,
      @Parameter(description = "Número da página (base 0)", example = "0") @RequestParam(defaultValue = "0") int pageNumber,
      @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
      @Parameter(description = "Direção da ordenação (ASC ou DESC)", example = "DESC") @RequestParam(defaultValue = "DESC") String sortDirection) {
    String sortProperty = "id"; // se não for especificado, ordena por ID crescente
    Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(pageNumber, size, Sort.by(direction, sortProperty));

    log.info(
        "Admin requisitou listagem de denúncias: page={}, size={}, sort={}, filtros(status={}, categoria={})",
        pageNumber, size, sortDirection, status, categoria);

    Page<DenunciaAdminResponseDto> denunciasPage = denunciaService.listarTodas(status, categoria, pageable);
    log.info("Retornadas {} denúncias para a página {}.", denunciasPage.getNumberOfElements(),
        pageNumber);
    return ResponseEntity.ok(denunciasPage);
  }

  /**
   * Atualiza uma denúncia existente.
   *
   * @param id             ID da denúncia a ser atualizada
   * @param dto            dados para atualização
   * @param authentication contexto de autenticação
   * @return denúncia atualizada
   */
  @PatchMapping("/{id}")
  @Operation(summary = "Atualiza o status de uma denúncia", description = "...")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Atualizada com sucesso", content = @Content(schema = @Schema(implementation = DenunciaAdminResponseDto.class))),
      @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada", content = @Content) })
  public ResponseEntity<DenunciaAdminResponseDto> atualizarDenuncia(
      @Parameter(description = "ID da denúncia") @PathVariable Long id,
      @Valid @RequestBody AtualizarDenunciaDto dto, Authentication authentication) {
    String adminName = authentication.getName();
    log.info("Admin {} requisitou atualização da denúncia ID {}", adminName, id);

    return denunciaService.atualizarDenuncia(id, dto, adminName).map(ResponseEntity::ok)
        .orElseGet(() -> {
          log.warn("Denúncia ID {} não encontrada para atualização pelo admin {}.", id, adminName);
          return ResponseEntity.notFound().build();
        });
  }

  /**
   * Exclui uma denúncia.
   *
   * @param id ID da denúncia a ser excluída
   * @return resposta sem conteúdo ou não encontrado
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Exclui uma denúncia", description = "Remove permanentemente uma denúncia do sistema.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Denúncia excluída com sucesso", content = @Content),
      @ApiResponse(responseCode = "401", description = "Acesso não autorizado. O token de autenticação (JWT) é inválido ou não foi fornecido.", content = @Content),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada", content = @Content) })
  public ResponseEntity<Void> deletarDenuncia(
      @Parameter(description = "ID da denúncia a ser excluída", required = true) @PathVariable Long id) {
    boolean deletado = denunciaService.deletarDenuncia(id);
    if (deletado) {
      log.info("Denúncia ID {} excluída com sucesso.", id);
      return ResponseEntity.noContent().build();
    } else {
      log.warn("Denúncia ID {} não encontrada para exclusão.", id);
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Lista os acompanhamentos de uma denúncia.
   *
   * @param id ID da denúncia
   * @return lista de acompanhamentos
   */
  @GetMapping("/{id}/acompanhamentos")
  @Operation(summary = "Lista os acompanhamentos de uma denúncia", description = "Retorna todo o histórico de acompanhamentos de uma denúncia específica.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Acompanhamentos listados com sucesso"),
      @ApiResponse(responseCode = "401", description = "Acesso não autorizado. O token de autenticação (JWT) é inválido ou não foi fornecido.", content = @Content),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada", content = @Content) })
  public ResponseEntity<List<AcompanhamentoDto>> listarAcompanhamentos(
      @Parameter(description = "ID da denúncia", required = true) @PathVariable Long id) {

    log.info("Listando acompanhamentos da denúncia ID {}", id);
    List<AcompanhamentoDto> acompanhamentos = denunciaService.listarAcompanhamentosPorId(id);
    log.info("Retornados {} acompanhamentos para a denúncia ID {}", acompanhamentos.size(), id);
    return ResponseEntity.ok(acompanhamentos);
  }

  /**
   * Adiciona um novo acompanhamento a uma denúncia.
   *
   * @param id                 ID da denúncia
   * @param novoAcompanhamento dados do novo acompanhamento
   * @param authentication     contexto de autenticação
   * @return acompanhamento criado
   */
  @PostMapping("/{id}/acompanhamentos")
  @Operation(summary = "Adiciona um novo acompanhamento a uma denúncia", description = "Permite que um administrador adicione uma nova mensagem "
      + "ou atualização ao histórico de uma denúncia.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Acompanhamento adicionado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AcompanhamentoDto.class))),
      @ApiResponse(responseCode = "401", description = "Acesso não autorizado. O token de autenticação (JWT) é inválido ou não foi fornecido.", content = @Content),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada", content = @Content) })
  public ResponseEntity<AcompanhamentoDto> adicionarAcompanhamento(
      @Parameter(description = "ID da denúncia", required = true) @PathVariable Long id,
      @Valid @RequestBody AcompanhamentoDto novoAcompanhamento, Authentication authentication) {
    log.info("Admin requisitou adicionar acompanhamento à denúncia ID {}", id);
    String nomeAdmin = authentication.getName();
    AcompanhamentoDto acompanhamentoSalvo = denunciaService.adicionarAcompanhamentoAdmin(id, novoAcompanhamento,
        nomeAdmin);
    log.info("Acompanhamento adicionado à denúncia ID {}: {}", id, acompanhamentoSalvo);
    return ResponseEntity.status(HttpStatus.CREATED).body(acompanhamentoSalvo);
  }
}
