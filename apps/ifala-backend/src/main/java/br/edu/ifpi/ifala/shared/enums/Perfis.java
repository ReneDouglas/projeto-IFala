package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os perfis de usuários no sistema. Cada perfil possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author Renê Morais
 * 
 * @author luisthedevmagician
 */
public enum Perfis {
  ADMIN("Admin"), ANONIMO("Usuário Anônimo");
  private final String displayName;

  Perfis(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
