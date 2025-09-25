package br.edu.ifpi.ifala.entities.enums;

public enum Perfis {
    ADMIN ("Admin"),
    ANONIMO ("Usuário Anônimo"),;

    private String descricao;

    Perfis(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
