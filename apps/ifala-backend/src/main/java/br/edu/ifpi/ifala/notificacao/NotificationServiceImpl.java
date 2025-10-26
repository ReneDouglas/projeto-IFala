package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import br.edu.ifpi.ifala.notificacao.enums.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private NotificationEmailService emailService;

    @Override
    public NotificationResponseDto create(NotificationRequestDto dto) {
        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo(dto.getTitulo());
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setTipo(dto.getTipo());

        Notificacao saved = repository.save(notificacao);

        if (saved.getTipo() == NotificationType.EXTERNAL) {
            log.info("📧 Enviando e-mail para nova denúncia cadastrada...");
            emailService.sendNewDenunciaEmail(saved);
        } else {
            log.info("🔔 Notificação interna criada (sem envio de e-mail).");
        }

        return NotificationResponseDto.fromEntity(saved);
    }

    @Override
    public List<NotificationResponseDto> findAll() {
        return repository.findAll()
                .stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDto update(Long id, NotificationRequestDto dto) {
        Notificacao notificacao = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada"));
        notificacao.setTitulo(dto.getTitulo());
        notificacao.setMensagem(dto.getMensagem());
        notificacao.setTipo(dto.getTipo());
        Notificacao updated = repository.save(notificacao);
        return NotificationResponseDto.fromEntity(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
