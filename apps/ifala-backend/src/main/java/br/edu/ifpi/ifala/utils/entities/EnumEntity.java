package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * Classe base para entidades de enumerações. Contém os campos comuns id, value e label.
 */
@MappedSuperclass
public abstract class EnumEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String value;

  @Column(nullable = false, length = 255)
  private String label;

  public EnumEntity() {
  }

  public EnumEntity(String value, String label) {
    this.value = value;
    this.label = label;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
