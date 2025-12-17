package br.edu.ifpi.ifala.notificacao;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificacaoService {

  /**
   * Lista as notificações não lidas com paginação.
   * 
   * @param pageable Parâmetros de paginação
   * @return Página de notificações não lidas
   */
  Page<Notificacao> listarNaoLidas(Pageable pageable);

  Optional<Notificacao> marcarComoLida(Long id, String usuario);

  void marcarComoLidaPorDenuncia(Long denunciaId, String usuario);

  boolean existsById(Long id);

  void deletar(Long id);

}
