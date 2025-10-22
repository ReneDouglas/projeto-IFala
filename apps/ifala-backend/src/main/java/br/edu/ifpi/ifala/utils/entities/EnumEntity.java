package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Classe base para entidades de enumerações. Contém os campos comuns id, value e label.
 * 
 * @author luisthedevmagician
 */

@MappedSuperclass
public abstract class EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 100)
  private String value;

  @Column(nullable = false, length = 255)
  private String label;

  public EnumEntity() {}

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    EnumEntity other = (EnumEntity) o;

    return id != null && id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }


  @Override
  public String toString() {
    return "EnumEntity{" + "id=" + id + ", value='" + value + '\'' + ", label='" + label + '\''
        + '}';
  }

}
