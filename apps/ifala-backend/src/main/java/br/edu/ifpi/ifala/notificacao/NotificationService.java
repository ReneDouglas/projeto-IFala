package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificationRequestDTO;
import br.edu.ifpi.ifala.notificacao.dto.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    NotificationResponseDTO create(NotificationRequestDTO dto);

    NotificationResponseDTO findById(Long id);

    List<NotificationResponseDTO> findAll();

    NotificationResponseDTO update(Long id, NotificationRequestDTO dto);

    void delete(Long id);
}
