package com.agencia.model;

public class Servico {
    private Long id;
    private String nome;
    private Integer duracao;
    private String descricao;

    public Servico() {}

    public Servico(String nome, Integer duracao, String descricao) {
        this.nome = nome;
        this.duracao = duracao;
        this.descricao = descricao;
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

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Servico{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", duracao=" + duracao +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
