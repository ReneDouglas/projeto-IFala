package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;
import br.edu.ifpi.ifala.autenticacao.UsuarioRepository;
import br.edu.ifpi.ifala.autenticacao.Usuario;
import br.edu.ifpi.ifala.notificacao.dto.EmailRequest;
import br.edu.ifpi.ifala.notificacao.enums.TiposNotificacao;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Serviço para gerenciar notificações externas no sistema.
 *
 * @author Phaola
 */

@Service
public class NotificacaoExternaServiceImpl implements NotificacaoExternaService {

  private static final Logger log = LoggerFactory.getLogger(NotificacaoExternaServiceImpl.class);

  private final EmailService emailService;
  private final UsuarioRepository usuarioRepository;
  private final NotificacaoRepository notificacaoRepository;

  @Value("${app.frontend.base-url:http://localhost:5173}")
  private String frontendBaseUrl;

  // Injeção de dependência via construtor
  public NotificacaoExternaServiceImpl(EmailService emailService,
      UsuarioRepository usuarioRepository, NotificacaoRepository notificacaoRepository) {
    this.emailService = emailService;
    this.usuarioRepository = usuarioRepository;
    this.notificacaoRepository = notificacaoRepository;
  }

  @Override
  public void notificarNovaDenuncia(Denuncia novaDenuncia) {
    // 1. Padrão de Título
    final String subject = "[IFala] Nova Denúncia Cadastrada";

    // 2. Corpo do E-mail (HTML Template) — usar token de acompanhamento (exibe últimas 3 chars)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String criadoEmStr =
        (novaDenuncia.getCriadoEm() != null) ? novaDenuncia.getCriadoEm().format(formatter) : "N/D";
    String detalheSituacao =
        (novaDenuncia.getStatus() != null) ? novaDenuncia.getStatus().name() : "N/D";
    final String body =
        buildNovaDenunciaBody(novaDenuncia.getTokenAcompanhamento(), criadoEmStr, detalheSituacao);

    // 3. Buscar E-mails de TODOS os usuários do sistema
    List<String> emails = usuarioRepository.findAll().stream().map(Usuario::getEmail)
        .filter(email -> email != null && !email.isBlank()).collect(Collectors.toList());

    // 4. Enviar E-mail para todos os usuários (usa DTO para suportar html/bcc)
    if (!emails.isEmpty()) {
      // Alteração: Envio via Cópia Oculta (BCC) para não expor os e-mails dos destinatários
      log.info(
          "Preparando envio de notificação de nova denúncia '{}' via BCC para {} destinatários.",
          subject, emails.size());

      // Mudar os destinatários de 'to' (primeiro parâmetro) para 'bcc' (terceiro parâmetro)
      EmailRequest req =
          new EmailRequest(new ArrayList<>(), new ArrayList<>(), emails, subject, body, true);

      // Persistir notificação (não-lida) para que apareça na lista do sistema
      try {
        Notificacao n = new Notificacao();
        n.setTipo(TiposNotificacao.NOVA_DENUNCIA);
        n.setConteudo(subject);
        n.setDenuncia(novaDenuncia);
        n.setLida(false);
        n.setDataEnvio(LocalDateTime.now());
        notificacaoRepository.save(n);
      } catch (Exception e) {
        log.warn("Não foi possível persistir Notificacao para nova denúncia ID {}: {}",
            novaDenuncia.getId(), e.getMessage());
      }
      try {
        emailService.sendEmail(req);
        log.info("Notificação externa enviada com sucesso para {} destinatários (denúncia ID {}).",
            emails.size(), novaDenuncia.getId());
      } catch (Exception e) {
        log.error("Falha ao enviar notificação externa (denúncia ID {}): {}", novaDenuncia.getId(),
            e.getMessage(), e);
      }
    }
  }


  @Override
  public void notificarNovaMensagem(Acompanhamento mensagem) {
    Denuncia denuncia = mensagem.getDenuncia();

    // 1. Padrão de Título (Com formatação do ID da Denúncia: *******XYZ)
    final String subject = String.format("[IFala] Nova Mensagem Recebida (Denúncia *******%s)",
        getShortDenunciaId(denuncia.getId()));

    // 2. Corpo do E-mail (HTML Template)
    final String body = buildNovaMensagemBody(denuncia.getTokenAcompanhamento(), mensagem);

    // 3. Buscar E-mails de TODOS os usuários do sistema
    List<String> emails = usuarioRepository.findAll().stream().map(Usuario::getEmail)
        .filter(email -> email != null && !email.isBlank()).collect(Collectors.toList());

    // 4. Enviar E-mail
    if (!emails.isEmpty()) {
      // Alteração: Envio via Cópia Oculta (BCC) para não expor os e-mails dos destinatários
      log.info(
          "Preparando envio de notificação de nova mensagem '{}' via BCC para {} destinatários.",
          subject, emails.size());

      // Mudar os destinatários de 'to' (primeiro parâmetro) para 'bcc' (terceiro parâmetro)
      EmailRequest req =
          new EmailRequest(new ArrayList<>(), new ArrayList<>(), emails, subject, body, true);

      // Persistir notificação (não-lida)
      try {
        Notificacao n = new Notificacao();
        n.setTipo(TiposNotificacao.NOVA_MENSAGEM);
        n.setConteudo(subject + " - " + getShortMessageText(mensagem.getMensagem()));
        n.setDenuncia(denuncia);
        n.setLida(false);
        n.setDataEnvio(LocalDateTime.now());
        notificacaoRepository.save(n);
      } catch (Exception e) {
        log.warn("Não foi possível persistir Notificacao para nova mensagem (denúncia ID {}) : {}",
            denuncia.getId(), e.getMessage());
      }
      try {
        emailService.sendEmail(req);
        log.info(
            "Notificação externa (mensagem) enviada com sucesso para {} destinatários (denúncia ID {}).",
            emails.size(), denuncia.getId());
      } catch (Exception e) {
        log.error("Falha ao enviar notificação externa (mensagem, denúncia ID {}): {}",
            denuncia.getId(), e.getMessage(), e);
      }
    }
  }

  private String buildNovaDenunciaBody(java.util.UUID tokenAcompanhamento, String criadoEm,
      String detalheSituacao) {
    String token = "*******" + getShortToken(tokenAcompanhamento);

    String painelLink = getFrontendUrl("/painel-denuncias");

    return """
        <!doctype html>
        <html>
        <head>
          <meta charset="utf-8">
          <style>
            body{font-family:Arial,Helvetica,sans-serif;color:#333}
            .card{max-width:600px;margin:20px auto;border:1px solid #e1e1e1;border-radius:8px;overflow:hidden}
            .header{background:#004d99;color:#fff;padding:16px}
            .content{padding:18px}
            .footer{background:#f6f6f6;padding:12px;text-align:center;color:#666;font-size:13px}
            /* Botão ajustado: background com a mesma cor do header (#004d99) para consistência, e negrito para alto contraste */
            .btn{display:inline-block;padding:10px 16px;background:#004d99;color:#fff;text-decoration:none;border-radius:6px;font-weight:bold}
          </style>
        </head>
        <body>
          <div class="card">
            <div class="header">
              <h2 style="margin:0;font-size:18px">IFala — Nova Denúncia Cadastrada</h2>
            </div>
            <div class="content">
              <p>Olá,</p>
              <p>Uma nova denúncia foi cadastrada no sistema IFala. Seguem os principais detalhes:</p>
              <ul>
                <li><strong>Token:</strong> %s</li>
                <li><strong>Data/Hora:</strong> %s</li>
                <li><strong>Situação:</strong> %s</li>
              </ul>
              <p>Para visualizar a denúncia completa e tomar as providências, acesse o painel do sistema.</p>
              <p><a class="btn" href="%s">Abrir Painel de Denúncias</a></p>
            </div>
            <!-- Mensagem de e-mail automático adicionada ao rodapé -->
            <div class="footer">Equipe IFala — <em>Notificações Automáticas</em><br/>Este é um e-mail automático. Por favor, não responda a esta mensagem.</div>
          </div>
        </body>
        </html>
        """
        .formatted(token, criadoEm, detalheSituacao, painelLink);
  }

  private String buildNovaMensagemBody(java.util.UUID tokenAcompanhamento,
      Acompanhamento mensagem) {
    String trecho = getShortMessageText(mensagem.getMensagem());
    String id = "*******" + getShortToken(tokenAcompanhamento);

    String painelLink = getFrontendUrl("/painel-denuncias");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    String dataEnvio =
        (mensagem.getDataEnvio() != null) ? mensagem.getDataEnvio().format(formatter) : "N/D";

    return """
        <!doctype html>
        <html>
        <head>
            <meta charset="utf-8">
            <style>
          body{font-family:Arial,Helvetica,sans-serif;color:#333}
          .card{max-width:600px;margin:20px auto;border:1px solid #e1e1e1;border-radius:8px;overflow:hidden}
          .header{background:#004d99;color:#fff;padding:16px}
          .content{padding:18px}
          .footer{background:#f6f6f6;padding:12px;text-align:center;color:#666;font-size:13px}
          .snippet{background:#fafafa;border-left:4px solid #007bff;padding:10px;margin:12px 0;border-radius:4px}
          /* Botão ajustado: background com a mesma cor do header (#004d99) para consistência, e negrito para alto contraste */
          .btn{display:inline-block;padding:10px 16px;background:#004d99;color:#fff;text-decoration:none;border-radius:6px;font-weight:bold}
            </style>
        </head>
        <body>
            <div class="card">
                <div class="header">
                    <h2 style="margin:0;font-size:18px">IFala — Nova Mensagem Recebida</h2>
                </div>
                <div class="content">
                    <p>Olá,</p>
                    <p>Recebemos uma nova mensagem relacionada à denúncia <strong>#%s</strong>. Segue um trecho para referência:</p>
                    <div class="snippet">
                        %s
                    </div>
                    <p>Esta mensagem foi enviada pelo denunciante. Acesse o sistema para ler a mensagem completa e prosseguir com o acompanhamento.</p>
                    <p><strong>Data/Hora da mensagem:</strong> %s</p>
                    <p><a class="btn" href="%s">Abrir Denúncia</a></p>
                </div>
                <div class="footer">Equipe IFala — <em>Notificações Automáticas</em><br/>Este é um e-mail automático. Por favor, não responda a esta mensagem.</div>
            </div>
        </body>
        </html>
        """
        .formatted(id, (trecho != null ? trecho : "(sem conteúdo)"), dataEnvio, painelLink);
  }


  private String getShortToken(java.util.UUID token) {
    if (token == null) {
      return "N/D";
    }
    String s = token.toString();
    if (s.length() <= 3) {
      return s;
    }
    return s.substring(s.length() - 3);
  }

  private String getFrontendUrl(String path) {
    if (path == null) {
      path = "";
    }
    String base = frontendBaseUrl != null ? frontendBaseUrl.trim() : "";
    if (base.isEmpty()) {
      return path;
    }
    if (base.endsWith("/")) {
      base = base.substring(0, base.length() - 1);
    }
    if (!path.startsWith("/")) {
      path = "/" + path;
    }
    return base + path;
  }

  private String getShortDenunciaId(Long id) {
    String idStr = String.valueOf(id);
    if (idStr.length() >= 3) {
      return idStr.substring(idStr.length() - 3);
    }
    return idStr;
  }

  private String getShortMessageText(String conteudo) {
    if (conteudo == null) {
      return null;
    }
    if (conteudo.length() > 200) {
      return conteudo.substring(0, 200) + "...";
    }
    return conteudo;
  }
}
