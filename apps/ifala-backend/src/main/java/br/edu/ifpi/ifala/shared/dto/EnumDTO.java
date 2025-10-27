package br.edu.ifpi.ifala.shared.dto;

/**
 * DTO (Data Transfer Object) utilizado para representar valores de enumerações de forma
 * simplificada, contendo um valor e um rótulo (label) para exibição.
 *
 * @author luisthedevmagician
 */

public class EnumDTO {
  private String value;
  private String label;

  public EnumDTO() {
  }

  public EnumDTO(String value, String label) {
    this.value = value;
    this.label = label;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
