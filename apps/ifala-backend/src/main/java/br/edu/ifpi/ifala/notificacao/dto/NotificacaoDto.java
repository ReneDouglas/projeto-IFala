package br.edu.ifpi.ifala.notificacao.dto;

import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de Notificação.
 *
 * @author Phaola
 */

public class NotificacaoDto {
  private Long id;
  private String conteudo;
  private TiposNotificacao tipo;
  private Long denunciaId;
  private Boolean lida;
  private String lidaPor;
  private LocalDateTime dataEnvio;

  public Long getId() {
    return id;
  }

  public String getConteudo() {
    return conteudo;
  }

  public TiposNotificacao getTipo() {
    return tipo;
  }

  public Long getDenunciaId() {
    return denunciaId;
  }

  public Boolean getLida() {
    return lida;
  }

  public String getLidaPor() {
    return lidaPor;
  }

  public LocalDateTime getDataEnvio() {
    return dataEnvio;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setConteudo(String conteudo) {
    this.conteudo = conteudo;
  }

  public void setTipo(TiposNotificacao tipo) {
    this.tipo = tipo;
  }

  public void setDenunciaId(Long denunciaId) {
    this.denunciaId = denunciaId;
  }

  public void setLida(Boolean lida) {
    this.lida = lida;
  }

  public void setLidaPor(String lidaPor) {
    this.lidaPor = lidaPor;
  }

  public void setDataEnvio(LocalDateTime dataEnvio) {
    this.dataEnvio = dataEnvio;
  }
}
