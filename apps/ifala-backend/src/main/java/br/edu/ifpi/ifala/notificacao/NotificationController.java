package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notificacoes")
public class NotificationController {

  @Autowired
  private NotificationService service;

  @PostMapping
  public ResponseEntity<NotificationResponseDto> create(@RequestBody NotificationRequestDto dto) {
    return ResponseEntity.status(201).body(service.create(dto));
  }

  @GetMapping
  public ResponseEntity<List<NotificationResponseDto>> getAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<NotificationResponseDto> update(
      @PathVariable Long id,
      @RequestBody NotificationRequestDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
