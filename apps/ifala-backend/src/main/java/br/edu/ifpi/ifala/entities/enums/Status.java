package br.edu.ifpi.ifala.entities.enums;

public enum Status {
    RECEBIDO ("Recebido"),
    EM_ANALISE ("Em Análise"),
    RESOLVIDO ("Resolvido"),
    REJEITADO ("Rejeitado");

    private String descricao;

    Status(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
