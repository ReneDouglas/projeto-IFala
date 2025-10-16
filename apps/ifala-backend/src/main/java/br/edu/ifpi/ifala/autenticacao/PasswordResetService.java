package br.edu.ifpi.ifala.autenticacao;

public interface PasswordResetService {
  void sendPasswordReset(Usuario user);
}
