package br.edu.ifpi.ifala.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import br.edu.ifpi.ifala.entities.enums.Categorias;
import br.edu.ifpi.ifala.entities.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated
    private Categorias categoria;

    @Enumerated
    private Status status;

    @Column(name = "token_acompanhamento", unique = true, updatable = false, nullable = false)
    private UUID tokenAcompanhamento;

    @Column(name = "criado_em", updatable = false, nullable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "denuncia")
    private Set<Acompanhamento> acompanhamentos = new HashSet<>();

    public Denuncia() {
        this.tokenAcompanhamento = UUID.randomUUID();
        this.status = Status.RECEBIDO;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public Categorias getCategoria() {
        return categoria;
    }
    public void setCategoria(Categorias categoria) {
        this.categoria = categoria;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
    public UUID getTokenAcompanhamento() {
        return tokenAcompanhamento;
    }
    public void setTokenAcompanhamento(UUID tokenAcompanhamento) {
        this.tokenAcompanhamento = tokenAcompanhamento;
    }
    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
    public Set<Acompanhamento> getAcompanhamentos() {
        return acompanhamentos;
    }
    public void setAcompanhamentos(Set<Acompanhamento> acompanhamentos) {
        this.acompanhamentos = acompanhamentos;
    }
}
