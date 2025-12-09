package br.edu.ifpi.ifala.utils;

import br.edu.ifpi.ifala.shared.dto.EnumDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que fornece endpoints para obter listas de enumerações utilizadas no sistema,
 * como status, categorias, graus, cursos e turmas. Cada endpoint retorna uma lista de DTOs
 * representando os valores das enumerações armazenadas no banco de dados.
 *
 * @author luisthedevmagician
 */

@RestController
@RequestMapping("/api/v1/utils")
public class UtilsController {

  private final UtilsService utilsService;

  public UtilsController(UtilsService utilsService) {
    this.utilsService = utilsService;
  }

  // Retorna todos os status disponíveis no sistema.
  @GetMapping("/status")
  public ResponseEntity<List<EnumDTO>> getStatus() {
    return ResponseEntity.ok(utilsService.getAllStatus());
  }

  // Retorna todas as categorias disponíveis no sistema.
  @GetMapping("/categorias")
  public ResponseEntity<List<EnumDTO>> getCategorias() {
    return ResponseEntity.ok(utilsService.getAllCategorias());
  }

  // Retorna todos os graus disponíveis no sistema.
  @GetMapping("/graus")
  public ResponseEntity<List<EnumDTO>> getGraus() {
    return ResponseEntity.ok(utilsService.getAllGraus());
  }

  // Retorna todos os cursos disponíveis no sistema.
  @GetMapping("/cursos")
  public ResponseEntity<List<EnumDTO>> getCursos() {
    return ResponseEntity.ok(utilsService.getAllCursos());
  }

  // Retorna todos os anos disponíveis no sistema.
  @GetMapping("/anos")
  public ResponseEntity<List<EnumDTO>> getAnos() {
    return ResponseEntity.ok(utilsService.getAllAnos());
  }

  // Retorna todas as turmas disponíveis no sistema.
  @GetMapping("/turmas")
  public ResponseEntity<List<EnumDTO>> getTurmas() {
    return ResponseEntity.ok(utilsService.getAllTurmas());
  }
}
