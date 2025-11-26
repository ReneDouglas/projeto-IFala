package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import java.util.List;
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
   * Lista as notificações não lidas mais antigas (máximo 10). Retorna apenas notificações não lidas
   * para otimizar performance e UX.
   * 
   * @return Lista de DTOs com no máximo 10 notificações não lidas
   */
  @GetMapping
  public List<NotificacaoDto> listar() {
    List<Notificacao> list = service.listarNaoLidas();
    return list.stream().map(this::toDto).toList();
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
