package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa os perfis de usuários no sistema. Cada perfil possui uma descrição
 * associada para facilitar a identificação.
 *
 * @author Renê Morais
 * 
 * @author luisthedevmagician
 * 
 * @author luisthedevmagician
 */
public enum Perfis {
  ADMIN("admin", "Admin", "Admin"), ANONIMO("anonimo", "Usuário Anônimo", "Usuário Anônimo");

  private final String value;
  private final String descricao;
  private final String displayName;

  Perfis(String value, String descricao, String displayName) {
    this.value = value;
    this.descricao = descricao;
    this.displayName = displayName;
  }

  public String getValue() {
    return value;
  }

  public String getDescricao() {
    return descricao;
  }

  public String getDisplayName() {
    return displayName;
  }

  /**
   * Converte o valor do banco (admin, anonimo) para o enum correspondente Aceita tanto lowercase
   * quanto UPPERCASE
   */
  public static Perfis fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (Perfis perfil : values()) {
      if (perfil.value.equalsIgnoreCase(value)) {
        return perfil;
      }
    }
    throw new IllegalArgumentException("Perfil inválido: " + value);
  }
}
