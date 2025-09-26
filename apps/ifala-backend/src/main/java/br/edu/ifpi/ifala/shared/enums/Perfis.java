package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os perfis de usuários no sistema. Cada perfil possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author Renê Morais
 */
public enum Perfis {
  ADMIN("Admin"), ANONIMO("Usuário Anônimo");

  private String descricao;

  Perfis(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
