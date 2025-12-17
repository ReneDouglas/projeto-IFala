package br.edu.ifpi.ifala.notificacao;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

  private final NotificacaoRepository repository;

  public NotificacaoServiceImpl(NotificacaoRepository repository) {
    this.repository = repository;
  }

  @Override
  public Page<Notificacao> listarNaoLidas(final Pageable pageable) {
    // Retorna notificações não lidas paginadas do repositório
    return repository.findLidaFalseOrderByDataEnvioDesc(pageable);
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
