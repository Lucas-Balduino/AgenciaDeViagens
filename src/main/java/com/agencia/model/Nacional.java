package com.agencia.model;

public class Nacional extends Cliente {
    private String cpf;

    public Nacional() {}

    public Nacional(String nome, String telefone, String email, String cpf) {
        super(nome, telefone, email);
        this.cpf = cpf;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    @Override
    public String toString() {
        return "\nCliente Nacional:" +
               "\nID: " + getId() +
               "\nNome: " + getNome() +
               "\nTelefone: " + getTelefone() +
               "\nEmail: " + getEmail() +
               "\nCPF: " + cpf;
    }

}
