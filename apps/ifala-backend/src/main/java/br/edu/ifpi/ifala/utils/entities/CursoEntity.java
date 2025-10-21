package br.edu.ifpi.ifala.utils.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * Entidade JPA que representa a tabela enum_cursos no banco de dados.
 * 
 * @author luisthedevmagician
 */

@Entity
@Table(name = "enum_cursos")
public class CursoEntity extends EnumEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  public CursoEntity() {
    super();
  }

  public CursoEntity(String value, String label) {
    super(value, label);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CursoEntity)) {
      return false;
    }
    CursoEntity other = (CursoEntity) o;

    return getId() != null && getId().equals(other.getId());
  }

  @Override
  public String toString() {

    return "CursoEntity{" + "id=[" + getId() + "]" + ", value=[" + getValue() + "]" + ", label=["
        + getLabel() + "]" + "}";
  }
}
