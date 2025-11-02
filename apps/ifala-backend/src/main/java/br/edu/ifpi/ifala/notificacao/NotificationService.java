package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDto;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDto;
import java.util.List;

public interface NotificationService {
    NotificationResponseDto create(NotificationRequestDto dto);
    NotificationResponseDto findById(Long id);
    List<NotificationResponseDto> findAll();
    NotificationResponseDto update(Long id, NotificationRequestDto dto);
    void delete(Long id);
}
