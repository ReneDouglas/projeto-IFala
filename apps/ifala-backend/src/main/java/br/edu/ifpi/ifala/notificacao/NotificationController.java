package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST para operações de notificação.
 *
 * Apenas endereça chamadas básicas de CRUD e delega para {@link NotificationService}.
 */
@RestController
@RequestMapping("/notificacoes")
public class NotificationController {

  private final NotificationService service;

  /**
   * Construtor que injeta o serviço de notificação.
   *
   * @param service serviço de notificação
   */
  @Autowired
  public NotificationController(NotificationService service) {
    this.service = service;
  }

  /**
   * Cria uma nova notificação.
   *
   * @param dto dados da notificação a ser criada
   * @return ResponseEntity com status 201 e a notificação criada no corpo
   */
  @PostMapping
  public ResponseEntity<NotificationResponseDto> create(@RequestBody NotificationRequestDto dto) {
    return ResponseEntity.status(201).body(service.create(dto));
  }

  /**
   * Lista todas as notificações.
   *
   * @return lista de notificações
   */
  @GetMapping
  public ResponseEntity<List<NotificationResponseDto>> getAll() {
    return ResponseEntity.ok(service.findAll());
  }

  /**
   * Atualiza uma notificação existente.
   *
   * @param id id da notificação
   * @param dto dados a atualizar
   * @return notificação atualizada
   */
  @PatchMapping("/{id}")
  public ResponseEntity<NotificationResponseDto> update(
      @PathVariable Long id,
      @RequestBody NotificationRequestDto dto) {
    return ResponseEntity.ok(service.update(id, dto));
  }

  /**
   * Remove uma notificação.
   *
   * @param id id da notificação a remover
   * @return resposta vazia com status 204
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
/**
 * Controlador responsável por gerenciar as notificações.
 * Fornece endpoints para criar, listar, atualizar e deletar notificações.
 *
 * @author Lua
 */
