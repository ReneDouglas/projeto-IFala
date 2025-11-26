package br.edu.ifpi.ifala.notificacao;

import java.util.List;
import java.util.Optional;

public interface NotificacaoService {

  /**
   * Lista as notificações não lidas mais antigas (limitado a 10).
   * 
   * @return Lista com no máximo 10 notificações não lidas
   */
  List<Notificacao> listarNaoLidas();

  Optional<Notificacao> marcarComoLida(Long id, String usuario);

  void marcarComoLidaPorDenuncia(Long denunciaId, String usuario);

  boolean existsById(Long id);

  void deletar(Long id);

}
