package com.agencia.model;

public class Pacote {
    private Long id;
    private String nome;
    private String destino;
    private Integer duracao;
    private Double preco;

    public Pacote() {}

    public Pacote(String nome, String destino, Integer duracao, Double preco) {
        this.nome = nome;
        this.destino = destino;
        this.duracao = duracao;
        this.preco = preco;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "Pacote{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", destino='" + destino + '\'' +
                ", duracao=" + duracao +
                ", preco=" + preco +
                '}';
    }
}
