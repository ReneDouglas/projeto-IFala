package br.edu.ifpi.ifala.notificacao;

import java.util.List;
import java.util.stream.Collectors;
import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementação dos serviços de notificação.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

  private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

  private final NotificationRepository repository;
  private final NotificationEmailService emailService;

  /**
   * Construtor que injeta os serviços necessários.
   *
   * @param repository repositório de notificações
   * @param emailService serviço de e-mail
   */
  @Autowired
  public NotificationServiceImpl(NotificationRepository repository, NotificationEmailService emailService) {
    this.repository = repository;
    this.emailService = emailService;
  }

  /**
   * Cria uma nova notificação e dispara envio de e-mail se for do tipo EXTERNO.
   *
   * @param dto dados da notificação
   * @return DTO de resposta representando a entidade criada
   */
  @Override
  public NotificationResponseDto create(NotificationRequestDto dto) {
    Notificacao notificacao = new Notificacao();
    notificacao.setTitulo(dto.getTitulo());
    notificacao.setMensagem(dto.getMensagem());
    notificacao.setTipo(dto.getTipo());

    Notificacao saved = repository.save(notificacao);

    if (saved.getTipo() == TiposNotificacao.EXTERNO) {
      log.info("Enviando e-mail para nova denúncia cadastrada (tipo EXTERNO)");
      emailService.sendNewDenunciaEmail(saved);
    } else {
      log.info("Notificação interna criada (sem envio de e-mail).");
    }

    return NotificationResponseDto.fromEntity(saved);
  }

  /**
   * Recupera todas as notificações persistidas.
   *
   * @return lista de DTOs de notificação
   */
  @Override
  public List<NotificationResponseDto> findAll() {
    return repository.findAll()
      .stream()
      .map(NotificationResponseDto::fromEntity)
      .collect(Collectors.toList());
  }

  /**
   * Atualiza uma notificação existente.
   *
   * @param id identificador da notificação
   * @param dto dados a atualizar
   * @return DTO com os dados atualizados
   */
  @Override
  public NotificationResponseDto update(Long id, NotificationRequestDto dto) {
    Notificacao notificacao = repository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Notificação não encontrada"));

    notificacao.setTitulo(dto.getTitulo());
    notificacao.setMensagem(dto.getMensagem());
    notificacao.setTipo(dto.getTipo());
    Notificacao updated = repository.save(notificacao);
    return NotificationResponseDto.fromEntity(updated);
  }

  /**
   * Remove uma notificação pelo seu id.
   * Se a notificação não existir, nenhuma ação será executada.
   *
   * @param id identificador da notificação a ser removida
   */
  @Override
  public void delete(Long id) {
    if (repository.existsById(id)) {
      repository.deleteById(id);
      log.info("Notificação {} removida com sucesso", id);
    } else {
      log.warn("Tentativa de remover notificação inexistente: {}", id);
    }
  }
}
