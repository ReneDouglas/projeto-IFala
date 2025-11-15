package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import br.edu.ifpi.ifala.notificacao.enums.TipoNotificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository repository;
    private final NotificationEmailService emailService;

    public NotificationServiceImpl(NotificationRepository repository,
            NotificationEmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Override
    public NotificationResponseDto create(NotificationRequestDto dto) {
        Notificacao n = new Notificacao();
        n.setTitulo(dto.titulo());
        n.setMensagem(dto.mensagem());
        n.setTipo(dto.tipo());
        n.setCriadoEm(LocalDateTime.now()); // deixa explícito e mais claro no código

        Notificacao saved = repository.save(n);

        if (saved.getTipo() == TipoNotificacao.EXTERNO) {
            log.info("Enviando e-mail de notificação externa para nova denúncia...");
            emailService.sendNewDenunciaEmail(saved, "sistema-ifala-development@gmail.com");
        }

        return NotificationResponseDto.fromEntity(saved);
    }

    @Override
    public List<NotificationResponseDto> findAll() {
        return repository.findAll().stream().map(NotificationResponseDto::fromEntity).toList();
    }

    @Override
    public NotificationResponseDto findById(Long id) {
        Notificacao n = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notificação não encontrada"));

        return NotificationResponseDto.fromEntity(n);
    }

    @Override
    public NotificationResponseDto update(Long id, NotificationRequestDto dto) {
        Notificacao n = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notificação não encontrada"));

        if (dto.titulo() != null)
            n.setTitulo(dto.titulo());
        if (dto.mensagem() != null)
            n.setMensagem(dto.mensagem());
        if (dto.tipo() != null)
            n.setTipo(dto.tipo());

        Notificacao updated = repository.save(n);
        return NotificationResponseDto.fromEntity(updated);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada");
        }

        repository.deleteById(id);
    }
}
