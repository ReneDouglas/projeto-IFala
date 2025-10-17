package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.acompanhamento.acompanhamentoDTO.AcompanhamentoDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.AtualizarDenunciaDto;
import br.edu.ifpi.ifala.denuncia.denunciaDTO.DenunciaResponseDto;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import jakarta.validation.Valid;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Controller responsável pelos endpoints de ADMINISTRAÇÃO de denúncias. Requer autenticação e
 * autorização.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
@RestController
@RequestMapping("/api/v1/admin/denuncias")
@Tag(name = "Administração de Denúncias",
    description = "Endpoints restritos para administradores gerenciarem denúncias.")
@SecurityRequirement(name = "bearerAuth")
public class DenunciaAdminController {

  private final DenunciaService denunciaService;

  public DenunciaAdminController(DenunciaService denunciaService) {
    this.denunciaService = denunciaService;
  }

  @GetMapping
  @Operation(summary = "Lista todas as denúncias com filtros",
      description = "Retorna uma lista paginada de denúncias. Pode ser filtrada por status e/ou categoria.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de denúncias retornada com sucesso")})

  public ResponseEntity<Page<DenunciaResponseDto>> listarTodas(
      @Parameter(
          description = "Filtrar denúncias por status (ex: ABERTA, EM_ANDAMENTO)") @RequestParam(
              required = false) Status status,
      @Parameter(
          description = "Filtrar denúncias por categoria (ex: ASSÉDIO, BULLYING)") @RequestParam(
              required = false) Categorias categoria,
      Pageable pageable) {

    Page<DenunciaResponseDto> denunciasPage =
        denunciaService.listarTodas(status, categoria, pageable);
    return ResponseEntity.ok(denunciasPage);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Atualiza o status ou categoria de uma denúncia",
      description = "Permite que um administrador altere o status ou a categoria de uma denúncia existente.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Denúncia atualizada com sucesso",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = DenunciaResponseDto.class))),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})

  public ResponseEntity<DenunciaResponseDto> atualizarDenuncia(
      @Parameter(description = "ID da denúncia a ser atualizada",
          required = true) @PathVariable Long id,
      @Valid @RequestBody AtualizarDenunciaDto dto, Authentication authentication) {
    String adminName = authentication.getName();

    return denunciaService.atualizarDenuncia(id, dto, adminName).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Exclui uma denúncia",
      description = "Remove permanentemente uma denúncia do sistema.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Denúncia excluída com sucesso",
          content = @Content),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})
  public ResponseEntity<Void> deletarDenuncia(@Parameter(
      description = "ID da denúncia a ser excluída", required = true) @PathVariable Long id) {
    boolean deletado = denunciaService.deletarDenuncia(id);
    if (deletado) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/{id}/acompanhamentos")
  @Operation(summary = "Lista os acompanhamentos de uma denúncia",
      description = "Retorna todo o histórico de acompanhamentos de uma denúncia específica.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Acompanhamentos listados com sucesso"),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})

  public ResponseEntity<List<AcompanhamentoDto>> listarAcompanhamentos(
      @Parameter(description = "ID da denúncia", required = true) @PathVariable Long id) {
    List<AcompanhamentoDto> acompanhamentos = denunciaService.listarAcompanhamentosPorId(id);
    return ResponseEntity.ok(acompanhamentos);
  }

  @PostMapping("/{id}/acompanhamentos")
  @Operation(summary = "Adiciona um novo acompanhamento a uma denúncia",
      description = "Permite que um administrador adicione uma nova mensagem ou atualização ao histórico de uma denúncia.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Acompanhamento adicionado com sucesso",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = AcompanhamentoDto.class))),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})
  public ResponseEntity<AcompanhamentoDto> adicionarAcompanhamento(
      @Parameter(description = "ID da denúncia", required = true) @PathVariable Long id,
      @Valid @RequestBody AcompanhamentoDto novoAcompanhamento, Authentication authentication) {
    String nomeAdmin = authentication.getName(); // pega o nome do usuário autenticado
    AcompanhamentoDto acompanhamentoSalvo =
        denunciaService.adicionarAcompanhamentoAdmin(id, novoAcompanhamento, nomeAdmin);
    return ResponseEntity.status(HttpStatus.CREATED).body(acompanhamentoSalvo);
  }
}
