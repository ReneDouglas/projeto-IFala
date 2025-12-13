package br.edu.ifpi.ifala.notificacao;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

  private final NotificacaoRepository repository;

  public NotificacaoServiceImpl(NotificacaoRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<Notificacao> listarNaoLidas() {
    // Retorna TODAS as notificações não lidas (sem limite)
    // Frontend faz merge inteligente para melhor UX
    return repository.findAllByLidaFalseOrderByDataEnvioDesc();
  }

  @Override
  public Optional<Notificacao> marcarComoLida(Long id, String usuario) {
    return repository.findById(id).map(n -> {
      n.setLida(true);
      n.setLidaPor(usuario);
      return repository.save(n);
    });
  }

  @Override
  @Transactional
  public void marcarComoLidaPorDenuncia(Long denunciaId, String usuario) {
    repository.marcarComoLidaPorDenuncia(denunciaId, usuario);
  }

  @Override
  public boolean existsById(Long id) {
    return repository.existsById(id);
  }

  @Override
  public void deletar(Long id) {
    repository.deleteById(id);
  }
}
