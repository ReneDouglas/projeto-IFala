package br.edu.ifpi.ifala.acompanhamento;

import org.springframework.data.jpa.repository.JpaRepository;
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
  
}
