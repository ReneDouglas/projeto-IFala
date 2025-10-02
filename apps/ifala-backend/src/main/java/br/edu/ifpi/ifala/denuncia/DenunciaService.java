package br.edu.ifpi.ifala.denuncia;

import br.edu.ifpi.ifala.shared.enums.Categorias;
import br.edu.ifpi.ifala.shared.enums.Status;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Classe de serviço responsável por manipular operações relacionadas a
 * denúncias.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */
public class DenunciaService {
  private final DenunciaRepository denunciaRepository;

  public DenunciaService(DenunciaRepository denunciaRepository) {
    this.denunciaRepository = denunciaRepository;
  }

  @Transactional
  public Denuncia criarDenuncia(Denuncia novaDenuncia) {
    return denunciaRepository.save(novaDenuncia);
  }

  @Transactional()
  public Optional<Denuncia> consultarPorTokenAcompanhamento(UUID tokenAcompanhamento) {
    return denunciaRepository.findByTokenAcompanhamento(tokenAcompanhamento)
        .filter(denuncia -> denuncia.getStatus() != Status.RESOLVIDO &&
            denuncia.getStatus() != Status.REJEITADO);
  }

  /*
   * tipo Page é uma interface do Spring Data que encapsula uma página de dados
   * Pageable é uma interface que define a paginação e ordenação
   * Specification é uma interface do Spring Data JPA que permite construir
   * consultas dinamicamente
   * predicate é uma condição usada em consultas para filtrar resultados
   */

  @Transactional()
  public Page<Denuncia> listarTodas(Status status, Categorias categoria, Pageable pageable) {
    Specification<Denuncia> spec = (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }
      if (categoria != null) {
        predicates.add(criteriaBuilder.equal(root.get("categoria"), categoria));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };

    return denunciaRepository.findAll(spec, pageable);
  }

  @Transactional
  public Optional<Denuncia> atualizarDenuncia(Long id, Denuncia dadosParaAtualizar) {
    Optional<Denuncia> denunciaExistente = denunciaRepository.findById(id);

    if (denunciaExistente.isEmpty()) {
      return Optional.empty();
    }

    Denuncia denuncia = denunciaExistente.get();

    if (denuncia.getStatus() == Status.RESOLVIDO || denuncia.getStatus() == Status.REJEITADO) {
      throw new IllegalStateException("Denúncia já está em estado final e não pode ser alterada.");
    }

    if (dadosParaAtualizar.getStatus() != null) {
      denuncia.setStatus(dadosParaAtualizar.getStatus());
    }
    if (dadosParaAtualizar.getMotivoRejeicao() != null) {
      denuncia.setMotivoRejeicao(dadosParaAtualizar.getMotivoRejeicao());
    }

    denuncia.setAlteradoEm(LocalDateTime.now());

    return Optional.of(denunciaRepository.save(denuncia));
  }

  @Transactional
  public boolean deletarDenuncia(Long id) {
    Optional<Denuncia> denuncia = denunciaRepository.findById(id);
    if (denuncia.isPresent()) {
      denunciaRepository.delete(denuncia.get());
      return true;
    }
    return false;
  }

}
