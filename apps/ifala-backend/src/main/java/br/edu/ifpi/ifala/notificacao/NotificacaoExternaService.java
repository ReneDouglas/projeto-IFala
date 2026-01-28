package br.edu.ifpi.ifala.notificacao;

import br.edu.ifpi.ifala.denuncia.Denuncia;
import br.edu.ifpi.ifala.acompanhamento.Acompanhamento;

public interface NotificacaoExternaService {

  /**
   * Notifica todos os usuários do sistema via e-mail sobre o cadastro de uma nova denúncia.
   * 
   * @param novaDenuncia A denúncia recém-cadastrada.
   */
  void notificarNovaDenuncia(Denuncia novaDenuncia);

  /**
   * Notifica usuários via e-mail sobre uma nova mensagem recebida em uma denúncia existente.
   * 
   * @param mensagem A nova mensagem recebida.
   */
  void notificarNovaMensagem(Acompanhamento mensagem);

  /**
   * Envia e-mail de redefinição de senha para o usuário.
   * 
   * @param email E-mail do destinatário
   * @param resetLink Link de redefinição de senha
   */
  void enviarEmailRedefinicaoSenha(String email, String resetLink);

  /**
   * Notifica o denunciante sobre o registro da sua denúncia com o link de acompanhamento.
   */
  void notificarRegistroDenuncia(Denuncia denuncia);

  /**
   * Notifica o denunciante sobre uma alteração no status da sua denúncia.
   */
  void notificarAtualizacaoStatus(Denuncia denuncia);

  /**
   * Notifica o denunciante sobre uma nova resposta do administrador na sua denúncia.
   */
  void notificarNovaRespostaAdmin(Denuncia denuncia);
}
