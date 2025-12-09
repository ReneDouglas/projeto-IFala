package br.edu.ifpi.ifala.denuncia;

import java.io.Serializable;

import br.edu.ifpi.ifala.shared.enums.Ano;
import br.edu.ifpi.ifala.shared.enums.Curso;
import br.edu.ifpi.ifala.shared.enums.Grau;
import br.edu.ifpi.ifala.shared.enums.Turma;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "denunciantes")
public class Denunciante implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "O nome não pode ser vazio.")
  @Size(max = 255)
  @Column(name = "nome_completo")
  private String nomeCompleto;

  @NotBlank(message = "O email não pode ser vazio.")
  @Email(message = "Formato de email inválido.")
  @Size(max = 255)
  @Column(name = "email")
  private String email;

  @NotNull(message = "O grau não pode ser nulo.")
  @Enumerated(EnumType.STRING)
  @Column(name = "grau")
  private Grau grau;

  @NotNull(message = "O curso não pode ser nulo.")
  @Enumerated(EnumType.STRING)
  @Column(name = "curso")
  private Curso curso;

  // @NotNull(message = "O ano não pode ser nulo.")
  @Enumerated(EnumType.STRING)
  @Column(name = "ano")
  private Ano ano;

  @NotNull(message = "A turma não pode ser nula.")
  @Enumerated(EnumType.STRING)
  @Column(name = "turma")
  private Turma turma;

  public Denunciante() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNomeCompleto() {
    return nomeCompleto;
  }

  public void setNomeCompleto(String nomeCompleto) {
    this.nomeCompleto = nomeCompleto;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Grau getGrau() {
    return grau;
  }

  public void setGrau(Grau grau) {
    this.grau = grau;
  }

  public Curso getCurso() {
    return curso;
  }

  public void setCurso(Curso curso) {
    this.curso = curso;
  }

  public Ano getAno() {
    return ano;
  }

  public void setAno(Ano ano) {
    this.ano = ano;
  }

  public Turma getTurma() {
    return turma;
  }

  public void setTurma(Turma turma) {
    this.turma = turma;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Denunciante other = (Denunciante) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "Denunciante{ [id=" + id + " ]," + "Descrição= " + nomeCompleto + "]," + "Email=" + email
        + "]," + "Grau=" + grau + "]," + "Curso=" + curso + "]," + "Ano=" + ano + "]," + "Turma="
        + turma + "]" + "}";

  }
}
