package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;
import java.time.LocalDateTime;
import java.util.UUID;

// Dto apenas para administradores, incluindo o ID da denúncia
// diferente do DenunciaResponseDto que é usado para usuários comuns

/**
 * Data Transfer Object (DTO) para representar a resposta de uma denúncia com informações adicionais
 * para administradores.
 * 
 * @author Jhonatas G Ribeiro
 */
public record DenunciaAdminResponseDto(Long id, UUID tokenAcompanhamento, Status status,
    Categorias categoria, LocalDateTime criadoEm, LocalDateTime alteradoEm,
    Boolean temMensagemNaoLida, DenuncianteResponseDto denunciante) {
}
