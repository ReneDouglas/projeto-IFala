package br.edu.ifpi.ifala.denuncia;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Classe repositório para gerenciamento de entidades Denuncia. Responsável por
 * operações de
 * persistência e recuperação de denúncias no sistema.
 *
 * @author Renê Morais
 * @author Jhonatas G Ribeiro
 */

public interface DenunciaRepository
    extends JpaRepository<Denuncia, Long>, JpaSpecificationExecutor<Denuncia> {

  // Método para buscar uma denúncia pelo token de acompanhamento
  Optional<Denuncia> findByTokenAcompanhamento(UUID tokenAcompanhamento);

  // Os demais métodos de crud são herdados de JpaRepository
  /*
   * Os métodos de busca avançada são herdados de JpaSpecificationExecutor e
   * gerados automaticamente
   * pelo Spring Data JPA com base nas especificações fornecidas no service.
   */
}
