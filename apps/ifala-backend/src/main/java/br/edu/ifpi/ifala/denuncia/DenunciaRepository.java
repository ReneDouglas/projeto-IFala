package br.edu.ifpi.ifala.denuncia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

  /**
   * Busca IDs de denúncias usando a função SQL otimizada com índices GIN/trigram.
   * A função buscar_denuncias_por_texto busca em descrição e mensagens de acompanhamento.
   * 
   * @param termo Termo de busca (mínimo 3 caracteres)
   * @return Lista de IDs das denúncias que correspondem à busca
   */
  @Query(value = "SELECT * FROM buscar_denuncias_por_texto(:termo)", nativeQuery = true)
  List<Long> buscarIdsPorTexto(@Param("termo") String termo);

  // Os demais métodos de crud são herdados de JpaRepository
  /*
   * Os métodos de busca avançada são herdados de JpaSpecificationExecutor e
   * gerados automaticamente
   * pelo Spring Data JPA com base nas especificações fornecidas no service.
   */
}
