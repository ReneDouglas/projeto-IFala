package br.edu.ifpi.ifala.prova;

import br.edu.ifpi.ifala.prova.provaDTO.ProvaDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller público para operações com provas. Permite download de provas associadas a denúncias.
 *
 * @author Guilherme Alves
 */
@RestController
@RequestMapping("/api/v1/public/provas")
@Tag(name = "Provas Públicas",
    description = "Endpoints públicos para acesso a provas/evidências de denúncias")
public class ProvaPublicController {

  private static final Logger log = LoggerFactory.getLogger(ProvaPublicController.class);

  private final ProvaService provaService;

  public ProvaPublicController(ProvaService provaService) {
    this.provaService = provaService;
  }

  @GetMapping("/{provaId}")
  @Operation(summary = "Baixar uma prova específica",
      description = "Retorna o arquivo de uma prova/evidência pelo seu ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Arquivo encontrado e retornado"),
      @ApiResponse(responseCode = "404", description = "Prova não encontrada", content = @Content)})
  public ResponseEntity<Resource> downloadProva(
      @Parameter(description = "ID da prova", required = true) @PathVariable Long provaId) {

    log.info("Requisição de download de prova ID: {}", provaId);

    try {
      Prova prova = provaService.buscarPorId(provaId);
      Path caminhoArquivo = Paths.get(prova.getCaminhoArquivo());

      if (!Files.exists(caminhoArquivo)) {
        log.error("Arquivo não encontrado no sistema de arquivos: {}", caminhoArquivo);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Arquivo não encontrado no servidor");
      }

      Resource resource = new FileSystemResource(caminhoArquivo.toFile());

      return ResponseEntity.ok().contentType(MediaType.parseMediaType(prova.getTipoMime()))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "inline; filename=\"" + prova.getNomeArquivo() + "\"")
          .body(resource);

    } catch (ResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      log.error("Erro ao processar download da prova {}: {}", provaId, e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Erro ao processar download");
    }
  }

  @GetMapping("/denuncia/{denunciaId}")
  @Operation(summary = "Listar provas de uma denúncia",
      description = "Retorna a lista de todas as provas associadas a uma denúncia")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lista de provas retornada com sucesso"),
      @ApiResponse(responseCode = "404", description = "Denúncia não encontrada",
          content = @Content)})
  public ResponseEntity<List<ProvaDto>> listarProvasDenuncia(
      @Parameter(description = "ID da denúncia", required = true) @PathVariable Long denunciaId) {

    log.info("Listando provas da denúncia ID: {}", denunciaId);
    List<ProvaDto> provas = provaService.listarProvasPorDenuncia(denunciaId);
    return ResponseEntity.ok(provas);
  }
}
