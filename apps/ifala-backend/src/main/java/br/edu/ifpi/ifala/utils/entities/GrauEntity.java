package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Entidade JPA que representa a tabela enum_graus no banco de dados.
 * 
 * @author luisthedevmagician
 */


@Entity
@Table(name = "enum_graus")
public class GrauEntity extends EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public GrauEntity() {
    super();
  }

  public GrauEntity(String value, String label) {
    super(value, label);
  }

  @Override
  public String toString() {
    return "GrauEntity{" + "id=[" + getId() + "]" + ", value=[" + getValue() + "]" + ", label=["
        + getLabel() + "]" + "}";
  }
}
