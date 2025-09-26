package br.edu.ifpi.ifala.shared.enums;

/**
 * Enumeração que representa as categorias possíveis para uma denúncia no sistema. Cada categoria
 * possui uma descrição associada para facilitar a identificação.
 *
 * @author Renê Morais
 */
public enum Categorias {
  CELULAR("Porte de Celular"), DROGAS("Uso ou Porte de Drogas"), BULLYING(
      "Bullying e Assédio"), VIOLENCIA("Violência Física ou Verbal"), VANDALISMO(
          "Vandalismo e Danos ao Patrimônio"), ACADEMICO(
              "Questões Acadêmicas (Fraude, Plágio, etc.)"), OUTROS("Outros");

  private String descricao;

  Categorias(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
