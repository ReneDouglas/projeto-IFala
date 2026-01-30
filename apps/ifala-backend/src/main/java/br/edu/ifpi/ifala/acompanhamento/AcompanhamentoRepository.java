package br.edu.ifpi.ifala.acompanhamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AcompanhamentoRepository extends JpaRepository<Acompanhamento, Long> {

  // retorna todos os acompanhamentos de uma denúncia específica, ordenados pela
  // data de envio em ordem crescente
  List<Acompanhamento> findByDenunciaIdOrderByDataEnvioAsc(Long denunciaId);
  
  Optional<Acompanhamento> findTopByDenuncia_TokenAcompanhamentoOrderByDataEnvioDesc(UUID tokenAcompanhamento);
  
  // verifica se existe alguma mensagem não visualizada de um autor específico para uma denúncia
  boolean existsByDenunciaIdAndAutorAndVisualizadoFalse(Long denunciaId, br.edu.ifpi.ifala.shared.enums.Perfis autor);

  //verifica se existe qualquer mensagem de um autor (lida ou não)
  boolean existsByDenunciaIdAndAutor(Long denunciaId, br.edu.ifpi.ifala.shared.enums.Perfis autor);

  
  // marca todas as mensagens de um autor como visualizadas para uma denúncia
  @Modifying
  @Query("UPDATE Acompanhamento a SET a.visualizado = true WHERE a.denuncia.id = :denunciaId AND a.autor = :autor AND a.visualizado = false")
  void marcarComoVisualizadoPorDenunciaEAutor(@Param("denunciaId") Long denunciaId, @Param("autor") br.edu.ifpi.ifala.shared.enums.Perfis autor);
  
}
