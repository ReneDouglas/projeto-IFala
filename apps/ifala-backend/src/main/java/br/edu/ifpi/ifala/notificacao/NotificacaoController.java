package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gerenciar notificações no sistema.
 *
 * @author Phaola
 */

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

  private final NotificacaoRepository repository;


  @Autowired
  public NotificacaoController(NotificacaoRepository repository) {
    this.repository = repository;
  }

  @GetMapping
  public List<NotificacaoDto> listar(
      @RequestParam(value = "unreadOnly", required = false) Boolean unreadOnly) {
    List<Notificacao> list =
        Boolean.TRUE.equals(unreadOnly) ? repository.findByLidaFalse() : repository.findAll();
    return list.stream().map(this::toDto).toList();
  }

  @PutMapping("/{id}/ler")
  public ResponseEntity<NotificacaoDto> marcarComoLida(@PathVariable Long id) {
    return repository.findById(id).map(n -> {
      n.setLida(true);
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      String user = (auth != null) ? auth.getName() : null;
      n.setLidaPor(user);
      repository.save(n);
      return ResponseEntity.ok(toDto(n));
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/denuncia/{denunciaId}/ler")
  @Transactional
  public ResponseEntity<Void> marcarComoLidaPorDenuncia(@PathVariable Long denunciaId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String user = (auth != null) ? auth.getName() : null;
    try {
      repository.marcarComoLidaPorDenuncia(denunciaId, user);
      return ResponseEntity.noContent().build();
    } catch (Exception ex) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
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
