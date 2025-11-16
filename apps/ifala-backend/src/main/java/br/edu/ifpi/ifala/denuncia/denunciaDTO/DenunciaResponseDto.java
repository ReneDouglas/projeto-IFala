package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) para representar a resposta de uma denúncia.
 * 
 * @author Jhonatas G Ribeiro
 */
public record DenunciaResponseDto(Long id, UUID tokenAcompanhamento, Status status,
    Categorias categoria, LocalDateTime criadoEm) {
}
