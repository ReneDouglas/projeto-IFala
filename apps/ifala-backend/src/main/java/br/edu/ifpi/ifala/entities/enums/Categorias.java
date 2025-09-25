package br.edu.ifpi.ifala.entities.enums;

public enum Categorias {
    CELULAR ("Celular"),
    DROGAS ("Drogas"),
    BULLYING ("Bullying"),
    VIOLENCIA ("Violência"),
    OUTROS ("Outros");

    private String descricao;

    Categorias(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
