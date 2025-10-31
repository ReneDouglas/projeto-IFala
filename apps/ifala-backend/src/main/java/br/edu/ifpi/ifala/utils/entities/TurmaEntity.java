package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;


/**
 * Entidade JPA que representa a tabela enum_turmas no banco de dados.
 * 
 * @author luisthedevmagician
 */

@Entity
@Table(name = "enum_turmas")
public class TurmaEntity extends EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public TurmaEntity() {
    super();
  }

  public TurmaEntity(String value, String label) {
    super(value, label);
  }

  @Override
  public String toString() {
    return "TurmaEntity{" + "id=[" + getId() + "]" + ", value=[" + getValue() + "]" + ", label=["
        + getLabel() + "]}";
  }
}
