package br.edu.ifpi.ifala.denuncia.denunciaDTO;

// DTO para caso o denunciante queira se identificar
public class DadosDeIdentificacaoDto {

  private String nomeCompleto;
  private String email;
  private String grau;
  private String curso;
  private String turma;

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

  public String getGrau() {
    return grau;
  }

  public void setGrau(String grau) {
    this.grau = grau;
  }

  public String getCurso() {
    return curso;
  }

  public void setCurso(String curso) {
    this.curso = curso;
  }

  public String getTurma() {
    return turma;
  }

  public void setTurma(String turma) {
    this.turma = turma;
  }
}