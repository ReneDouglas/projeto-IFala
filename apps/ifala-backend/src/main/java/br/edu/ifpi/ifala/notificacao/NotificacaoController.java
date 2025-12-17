package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gerenciar notificações no sistema.
 *
 * @author Phaola
 */

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

  private final NotificacaoService service;

  public NotificacaoController(NotificacaoService service) {
    this.service = service;
  }

  /**
   * Lista as notificações não lidas com paginação.
   * 
   * @param pageable Parâmetros de paginação (page, size, sort)
   * @return Resposta paginada com notificações e metadados
   */
  @GetMapping
  public Map<String, Object> listar(@PageableDefault(size = 5, sort = "dataEnvio",
      direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
    Page<Notificacao> page = service.listarNaoLidas(pageable);

    Map<String, Object> response = new HashMap<>();
    response.put("items", page.getContent().stream().map(this::toDto).toList());
    response.put("totalItems", page.getTotalElements());
    response.put("totalPages", page.getTotalPages());
    response.put("currentPage", page.getNumber());
    response.put("pageSize", page.getSize());

    return response;
  }

  @PutMapping("/{id}/ler")
  public ResponseEntity<NotificacaoDto> marcarComoLida(@PathVariable Long id) {
    return service
        .marcarComoLida(id,
            (SecurityContextHolder.getContext().getAuthentication() != null)
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : null)
        .map(n -> ResponseEntity.ok(toDto(n))).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/denuncia/{denunciaId}/ler")
  public ResponseEntity<Void> marcarComoLidaPorDenuncia(@PathVariable Long denunciaId) {
    String user = (SecurityContextHolder.getContext().getAuthentication() != null)
        ? SecurityContextHolder.getContext().getAuthentication().getName()
        : null;
    try {
      service.marcarComoLidaPorDenuncia(denunciaId, user);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable Long id) {
    if (service.existsById(id)) {
      service.deletar(id);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }

  private NotificacaoDto toDto(Notificacao n) {
    if (n == null)
      return null;
    NotificacaoDto dto = new NotificacaoDto(n.getId(), n.getConteudo(), n.getTipo(),
        n.getDenuncia() != null ? n.getDenuncia().getId() : null, n.getLida(), n.getLidaPor(),
        n.getDataEnvio());
    return dto;
  }
}
