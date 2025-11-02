package br.edu.ifpi.ifala.notificacao;

import java.util.List;
import java.util.stream.Collectors;
import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        n.setTitulo(dto.getTitulo());
        n.setMensagem(dto.getMensagem());
        n.setTipo(dto.getTipo());

        Notificacao saved = repository.save(n);

        if (saved.getTipo() == TiposNotificacao.EXTERNO) {
            log.info("Enviando e-mail (tipo EXTERNO)");
            // Em produção, buscar destinatários reais.
            emailService.sendNewDenunciaEmail(saved, "sistema-ifala-development@gmail.com");
        }
        return NotificationResponseDto.fromEntity(saved);
    }

    @Override
    public NotificationResponseDto findById(Long id) {
        Notificacao n = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada")
        );
        return NotificationResponseDto.fromEntity(n);
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
        Notificacao n = repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notificação não encontrada")
        );
        if (dto.getTitulo() != null) n.setTitulo(dto.getTitulo());
        if (dto.getMensagem() != null) n.setMensagem(dto.getMensagem());
        if (dto.getTipo() != null) n.setTipo(dto.getTipo());

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
