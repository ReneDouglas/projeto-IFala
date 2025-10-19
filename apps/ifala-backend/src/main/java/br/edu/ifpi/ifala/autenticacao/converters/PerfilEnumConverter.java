package br.edu.ifpi.ifala.autenticacao.converters;

import br.edu.ifpi.ifala.shared.enums.Perfis;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

public class PerfilEnumConverter {

  public PGobject toPgObject(Perfis attribute) {
    if (attribute == null)
      return null;
    try {
      PGobject pg = new PGobject();
      pg.setType("perfis_enum");
      pg.setValue(attribute.name().toLowerCase());
      return pg;
    } catch (SQLException e) {
      throw new RuntimeException("Erro ao converter Perfis para PGobject", e);
    }
  }

  public Perfis fromPgObject(PGobject pg) {
    if (pg == null)
      return null;
    String v = pg.getValue();
    return v == null ? null : Perfis.valueOf(v.toUpperCase());
  }
}
