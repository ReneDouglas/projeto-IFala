package br.edu.ifpi.ifala.notificacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO para requisições de envio de e-mail.
 *
 * @author Phaola
 */

public record EmailRequest(
    // Lista de destinatários (ao menos um)
    @NotEmpty(
        message = "O campo 'to' é obrigatório e deve conter pelo menos um destinatário.") List<String> to,

    // Campos opcionais de cópia
    List<String> cc, List<String> bcc,

    @NotBlank(message = "O campo 'subject' é obrigatório.") String subject,

    @NotBlank(message = "O campo 'body' é obrigatório.") String body,

    // Indica se o corpo é HTML. Default true (usamos templates HTML por padrão).
    boolean html) {

}
