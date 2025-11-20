package br.edu.ifpi.ifala.notificacao;

import java.util.List;
import java.util.Optional;

public interface NotificacaoService {

  List<Notificacao> listar(Boolean unreadOnly);

  Optional<Notificacao> marcarComoLida(Long id, String usuario);

  void marcarComoLidaPorDenuncia(Long denunciaId, String usuario);

  boolean existsById(Long id);

  void deletar(Long id);

}
