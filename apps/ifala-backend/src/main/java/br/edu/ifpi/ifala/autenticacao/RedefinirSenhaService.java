package br.edu.ifpi.ifala.autenticacao;

public interface RedefinirSenhaService {
  void sendPasswordReset(Usuario user);
}
