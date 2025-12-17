package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.notificacao.dto.NotificacaoDto;
import br.edu.ifpi.ifala.notificacao.dto.PaginatedNotificacaoDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

  private final NotificacaoRepository repository;
  private final NotificacaoMapper mapper;

  public NotificacaoServiceImpl(NotificacaoRepository repository, NotificacaoMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public PaginatedNotificacaoDto listarNaoLidas(final Pageable pageable) {
    // Busca notificações não lidas paginadas e converte para DTO
    Page<Notificacao> page = repository.findLidaFalseOrderByDataEnvioDesc(pageable);
    return mapper.toPaginatedDto(page);
  }

  @Override
  public Optional<NotificacaoDto> marcarComoLidaDto(Long id, String usuario) {
    return repository.findById(id).map(n -> {
      n.setLida(true);
      n.setLidaPor(usuario);
      Notificacao saved = repository.save(n);
      return mapper.toDto(saved);
    });
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
