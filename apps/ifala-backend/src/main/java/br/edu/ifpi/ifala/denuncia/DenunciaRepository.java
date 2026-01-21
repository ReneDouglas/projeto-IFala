package br.edu.ifpi.ifala.denuncia;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
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

  
  @EntityGraph(attributePaths = {"acompanhamentos", "provas", "denunciante"})
  Optional<Denuncia> findByTokenAcompanhamento(UUID tokenAcompanhamento);

  @EntityGraph(attributePaths = {"acompanhamentos", "provas", "denunciante"})
  Optional<Denuncia> findById(Long id);

  /**
   * busca IDs de denúncias com paginação (primeiro passo da estratégia anti-N+1).
   * retorna apenas os IDs para depois buscar as entidades completas com relacionamentos.
   *
   * @param spec especificação para filtros
   * @param pageable configuração de paginação e ordenação
   * @return Page contendo apenas os IDs das denúncias
   */
  @Query("SELECT d.id FROM Denuncia d")
  Page<Long> findAllIds(Pageable pageable);

  /**
   * busca denúncias completas com relacionamentos pelos IDs.
   * segundo passo da estratégia anti-N+1 para listagem paginada.
   *
   * @param ids lista de IDs das denúncias
   * @return lista de denúncias com todos os relacionamentos carregados
   */
  @EntityGraph(attributePaths = {"acompanhamentos", "provas", "denunciante"})
  @Query("SELECT DISTINCT d FROM Denuncia d WHERE d.id IN :ids")
  List<Denuncia> findAllByIdWithRelations(@Param("ids") List<Long> ids);

  /**
   * busca IDs de denúncias ordenados por:  
   * 1. Se possui mensagens não lidas do ANONIMO (denunciante)
   * 2. Data de criação (mais recente primeiro)
   * 
   * @param pageable configuração de paginação
   * @return Page com os IDs ordenados
   */
  @Query("""
      SELECT d.id 
      FROM Denuncia d 
      LEFT JOIN d.acompanhamentos a 
      WHERE 1=1
      GROUP BY d.id, d.criadoEm
      ORDER BY 
        MAX(CASE WHEN a.autor = 'ANONIMO' AND a.visualizado = false THEN 1 ELSE 0 END) DESC,
        d.criadoEm DESC
      """)
  Page<Long> findAllIdsOrderedByNewMessagesAndDate(Pageable pageable);

  /**
   * busca IDs de denúncias com filtros e ordenação personalizada.
   * 
   * @param status filtro de status (opcional)
   * @param categoria filtro de categoria (opcional)
   * @param tokenSearch busca por token (opcional)
   * @param pageable configuração de paginação
   * @return Page com os IDs filtrados e ordenados
   */
  @Query("""
      SELECT d.id 
      FROM Denuncia d 
      LEFT JOIN d.acompanhamentos a 
      WHERE 
        (:status IS NULL OR d.status = :status) AND
        (:categoria IS NULL OR d.categoria = :categoria) AND
        (:tokenSearch IS NULL OR CAST(d.tokenAcompanhamento AS string) = :tokenSearch)
      GROUP BY d.id, d.criadoEm
      ORDER BY 
        MAX(CASE WHEN a.autor = 'ANONIMO' AND a.visualizado = false THEN 1 ELSE 0 END) DESC,
        d.criadoEm DESC
      """)
  Page<Long> findAllIdsWithFiltersOrderedByNewMessages(
      @Param("status") br.edu.ifpi.ifala.shared.enums.Status status,
      @Param("categoria") br.edu.ifpi.ifala.shared.enums.Categorias categoria,
      @Param("tokenSearch") String tokenSearch,
      Pageable pageable);

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
