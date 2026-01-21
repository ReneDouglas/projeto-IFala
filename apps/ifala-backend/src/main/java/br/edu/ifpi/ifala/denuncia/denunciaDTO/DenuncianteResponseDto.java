package br.edu.ifpi.ifala.denuncia.denunciaDTO;

import br.edu.ifpi.ifala.shared.enums.Ano;
import br.edu.ifpi.ifala.shared.enums.Curso;
import br.edu.ifpi.ifala.shared.enums.Grau;
import br.edu.ifpi.ifala.shared.enums.Turma;

/**
 * Data Transfer Object (DTO) para representar os dados do denunciante em uma resposta.
 * 
 * @author Phaola
 */
public record DenuncianteResponseDto(String nomeCompleto, Grau grau, Curso curso, Ano ano,
    Turma turma) {
}
