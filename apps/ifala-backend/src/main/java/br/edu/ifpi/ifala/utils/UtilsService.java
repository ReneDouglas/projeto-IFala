package br.edu.ifpi.ifala.utils;

import br.edu.ifpi.ifala.shared.dto.EnumDTO;
import br.edu.ifpi.ifala.utils.repositories.CategoriaRepository;
import br.edu.ifpi.ifala.utils.repositories.CursoRepository;
import br.edu.ifpi.ifala.utils.repositories.GrauRepository;
import br.edu.ifpi.ifala.utils.repositories.StatusRepository;
import br.edu.ifpi.ifala.utils.repositories.TurmaRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Serviço que fornece métodos para buscar enumerações do banco de dados e convertê-las para DTOs.
 * 
 * Os dados são buscados das tabelas enum_* e convertidos para EnumDTO para exposição via API.
 * Isso permite flexibilidade para adicionar/modificar valores sem recompilar a aplicação.
 *
 * @author luisthedevmagician
 */
@Service
public class UtilsService {

  private final StatusRepository statusRepository;
  private final CategoriaRepository categoriaRepository;
  private final GrauRepository grauRepository;
  private final CursoRepository cursoRepository;
  private final TurmaRepository turmaRepository;

  public UtilsService(StatusRepository statusRepository, CategoriaRepository categoriaRepository,
      GrauRepository grauRepository, CursoRepository cursoRepository,
      TurmaRepository turmaRepository) {
    this.statusRepository = statusRepository;
    this.categoriaRepository = categoriaRepository;
    this.grauRepository = grauRepository;
    this.cursoRepository = cursoRepository;
    this.turmaRepository = turmaRepository;
  }

 // Busca todos os status da tabela enum_status e converte para EnumDTO.
  public List<EnumDTO> getAllStatus() {
    return statusRepository.findAll().stream()
        .map(entity -> new EnumDTO(entity.getValue(), entity.getLabel()))
        .collect(Collectors.toList());
  }

  // Busca todas as categorias da tabela enum_categorias e converte para EnumDTO.
  public List<EnumDTO> getAllCategorias() {
    return categoriaRepository.findAll().stream()
        .map(entity -> new EnumDTO(entity.getValue(), entity.getLabel()))
        .collect(Collectors.toList());
  }

  // Busca todos os graus da tabela enum_graus e converte para EnumDTO.
  public List<EnumDTO> getAllGraus() {
    return grauRepository.findAll().stream()
        .map(entity -> new EnumDTO(entity.getValue(), entity.getLabel()))
        .collect(Collectors.toList());
  }

  // Busca todos os cursos da tabela enum_cursos e converte para EnumDTO.
  public List<EnumDTO> getAllCursos() {
    return cursoRepository.findAll().stream()
        .map(entity -> new EnumDTO(entity.getValue(), entity.getLabel()))
        .collect(Collectors.toList());
  }

  // Busca todas as turmas da tabela enum_turmas e converte para EnumDTO.
  public List<EnumDTO> getAllTurmas() {
    return turmaRepository.findAll().stream()
        .map(entity -> new EnumDTO(entity.getValue(), entity.getLabel()))
        .collect(Collectors.toList());
  }
}
