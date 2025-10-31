package br.edu.ifpi.ifala.utils;

import br.edu.ifpi.ifala.shared.dto.EnumDTO;
import br.edu.ifpi.ifala.shared.enums.Status;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Grau;
import br.edu.ifpi.ifala.shared.enums.Curso;
import br.edu.ifpi.ifala.shared.enums.Turma;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Serviço que fornece métodos para converter enumerações Java para DTOs.
 * 
 * Converte os enums do sistema para EnumDTO para exposição via API. Implementação corrigida para
 * usar enums Java conforme especificação da Task #74.
 *
 * @author luisthedevmagician
 * @author GitHub Copilot (correção)
 */
@Service
public class UtilsService {

  // Converte todos os valores do enum Status para EnumDTO
  public List<EnumDTO> getAllStatus() {
    return Arrays.stream(Status.values())
        .map(status -> new EnumDTO(status.name(), status.getDisplayName()))
        .collect(Collectors.toList());
  }

  // Converte todos os valores do enum Categorias para EnumDTO
  public List<EnumDTO> getAllCategorias() {
    return Arrays.stream(Categorias.values())
        .map(categoria -> new EnumDTO(categoria.name(), categoria.getDisplayName()))
        .collect(Collectors.toList());
  }

  // Converte todos os valores do enum Grau para EnumDTO
  public List<EnumDTO> getAllGraus() {
    return Arrays.stream(Grau.values()).map(grau -> new EnumDTO(grau.name(), grau.getDisplayName()))
        .collect(Collectors.toList());
  }

  // Converte todos os valores do enum Curso para EnumDTO
  public List<EnumDTO> getAllCursos() {
    return Arrays.stream(Curso.values())
        .map(curso -> new EnumDTO(curso.name(), curso.getDisplayName()))
        .collect(Collectors.toList());
  }

  // Converte todos os valores do enum Turma para EnumDTO
  public List<EnumDTO> getAllTurmas() {
    return Arrays.stream(Turma.values())
        .map(turma -> new EnumDTO(turma.name(), turma.getDisplayName()))
        .collect(Collectors.toList());
  }
}
