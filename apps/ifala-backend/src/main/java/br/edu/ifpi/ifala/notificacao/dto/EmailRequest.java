package br.edu.ifpi.ifala.notificacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para requisições de envio de e-mail.
 *
 * @author Phaola
 */

public class EmailRequest {

  // Lista de destinatários (ao menos um)
  @NotEmpty(message = "O campo 'to' é obrigatório e deve conter pelo menos um destinatário.")
  private List<String> to = new ArrayList<>();

  // Campos opcionais de cópia
  private List<String> cc = new ArrayList<>();
  private List<String> bcc = new ArrayList<>();

  @NotBlank(message = "O campo 'subject' é obrigatório.")
  private String subject;

  @NotBlank(message = "O campo 'body' é obrigatório.")
  private String body;

  // Indica se o corpo é HTML. Default true (usamos templates HTML por padrão).
  private boolean html = true;

  public List<String> getTo() {
    return to;
  }

  public void setTo(List<String> to) {
    this.to = to;
  }

  // Métodos Getters e Setters para 'subject'
  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  // Métodos Getters e Setters para 'body'
  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public boolean isHtml() {
    return html;
  }

  public void setHtml(boolean html) {
    this.html = html;
  }

  // Métodos Getters e Setters para 'cc'
  public List<String> getCc() {
    return cc;
  }

  public void setCc(List<String> cc) {
    this.cc = cc;
  }

  // Métodos Getters e Setters para 'bcc'
  public List<String> getBcc() {
    return bcc;
  }

  public void setBcc(List<String> bcc) {
    this.bcc = bcc;
  }
}
