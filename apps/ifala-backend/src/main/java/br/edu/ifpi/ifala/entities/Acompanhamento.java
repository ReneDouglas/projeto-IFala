package br.edu.ifpi.ifala.entities;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

public class Acompanhamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String autor; // pode ser o denunciante ou o administrador
    private String mensagem;

    @ManyToOne
    private Denuncia denuncia; // referência à denúncia

    private LocalDateTime dataEnvio;

    public Acompanhamento() {
        this.dataEnvio = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public Denuncia getDenuncia() {
        return denuncia;
    }
    public void setDenuncia(Denuncia denuncia) {
        this.denuncia = denuncia;
    }
    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }
    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

}
