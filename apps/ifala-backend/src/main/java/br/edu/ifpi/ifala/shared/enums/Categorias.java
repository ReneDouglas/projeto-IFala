package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa as categorias possíveis para uma denúncia no sistema. Cada categoria
 * possui uma descrição associada para facilitar a identificação.
 *
 * @author Renê Morais
 * 
 * alterado por:
 * 
 * @author luisthedevmagician
 */
public enum Categorias {
  BULLYING("Bullying e Assédio"), DROGAS("Uso ou Porte de Substâncias Ilícitas"), VIOLENCIA(
    "Violência Física ou Verbal"), VANDALISMO("Vandalismo e Danos ao Patrimônio"), ACADEMICO(
      "Questões Acadêmicas (Fraude, Plágio)"), DISPOSITIVO_ELETRONICO(
        "Uso ou Porte de Dispositivo Eletrônico"), OUTROS("Outros");

  private final String displayName;

  Categorias(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }
}
