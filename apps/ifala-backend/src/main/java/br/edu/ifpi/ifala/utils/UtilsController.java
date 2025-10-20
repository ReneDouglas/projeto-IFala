package br.edu.ifpi.ifala.utils;

import br.edu.ifpi.ifala.shared.dto.EnumDTO;
import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Curso;
import br.edu.ifpi.ifala.shared.enums.Grau;
import br.edu.ifpi.ifala.shared.enums.Status;
import br.edu.ifpi.ifala.shared.enums.Turma;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que fornece endpoints para obter listas de enumerações utilizadas no sistema,
 * como status, categorias, graus, cursos e turmas. Cada endpoint retorna uma lista de DTOs
 * representando os valores das enumerações.
 *
 * @author luisthedevmagician
 */

@RestController
@RequestMapping("/api/v1/utils")
public class UtilsController {

  @GetMapping("/status")
  public List<EnumDTO> getStatus() {
    return Arrays.stream(Status.values())
        .map(s -> new EnumDTO(s.name(), s.getDisplayName()))
        .collect(Collectors.toList());
  }

  @GetMapping("/categorias")
  public List<EnumDTO> getCategorias() {
    return Arrays.stream(Categorias.values())
        .map(c -> new EnumDTO(c.name(), c.getDisplayName()))
        .collect(Collectors.toList());
  }

  @GetMapping("/graus")
  public List<EnumDTO> getGraus() {
    return Arrays.stream(Grau.values())
        .map(g -> new EnumDTO(g.name(), g.getDisplayName()))
        .collect(Collectors.toList());
  }

  @GetMapping("/cursos")
  public List<EnumDTO> getCursos() {
    return Arrays.stream(Curso.values())
        .map(c -> new EnumDTO(c.name(), c.getDisplayName()))
        .collect(Collectors.toList());
  }

  @GetMapping("/turmas")
  public List<EnumDTO> getTurmas() {
    return Arrays.stream(Turma.values())
        .map(t -> new EnumDTO(t.name(), t.getDisplayName()))
        .collect(Collectors.toList());
  }
}
