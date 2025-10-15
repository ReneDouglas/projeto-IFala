package br.edu.ifpi.ifala.acompanhamento;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AcompanhamentoRepository extends JpaRepository<Acompanhamento, Long> {

  // retorna todos os acompanhamentos de uma denúncia específica, ordenados pela
  // data de envio em ordem crescente
  List<Acompanhamento> findByDenunciaIdOrderByDataEnvioAsc(Long denunciaId);

  // retorna todos os acompanhamentos de uma denúncia específica via token,
  // ordenados pela data de envio em ordem decrescente
  List<Acompanhamento> findByDenunciaTokenOrderByDataEnvioDesc(UUID token);
}