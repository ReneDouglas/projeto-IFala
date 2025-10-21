package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Entidade JPA que representa a tabela enum_status no banco de dados.
 * 
 * @author luisthedevmagician
 */


@Entity
@Table(name = "enum_status")
public class StatusEntity extends EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public StatusEntity() {
    super();
  }

  public StatusEntity(String value, String label) {
    super(value, label);
  }

  @Override
  public String toString() {
    return "StatusEntity{" + "id=[" + getId() + "]" + ", value=[" + getValue() + "]" + ", label=["
        + getLabel() + "]" + "}";
  }
}
