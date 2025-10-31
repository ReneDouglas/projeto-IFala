package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.Notificacao;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import java.time.LocalDateTime;

/**
 * DTO para retornar informações de notificações em respostas.
 */
public class NotificationResponseDto {

  private Long id;
  private String titulo; 
  private String mensagem;
  private TiposNotificacao tipo;
  private LocalDateTime criadoEm;

  /**
   * Converte uma entidade Notificacao para NotificationResponseDto.
   *
   * @param entity A entidade a ser convertida
   * @return O DTO correspondente 
   */
  public static NotificationResponseDto fromEntity(Notificacao entity) {
    NotificationResponseDto dto = new NotificationResponseDto();
    dto.setId(entity.getId());
    dto.setTitulo(entity.getTitulo());
    dto.setMensagem(entity.getMensagem());
    dto.setTipo(entity.getTipo());
    dto.setCriadoEm(entity.getCriadoEm());
    return dto;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public TiposNotificacao getTipo() {
    return tipo;
  }

  public void setTipo(TiposNotificacao tipo) {
    this.tipo = tipo;
  }

  public LocalDateTime getCriadoEm() {
    return criadoEm;
  }

  public void setCriadoEm(LocalDateTime criadoEm) {
    this.criadoEm = criadoEm;
  }
}
