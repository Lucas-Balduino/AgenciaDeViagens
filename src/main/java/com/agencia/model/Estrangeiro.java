package com.agencia.model;

public class Estrangeiro extends Cliente {
    private String passaporte;

    public Estrangeiro() {}

    public Estrangeiro(String nome, String telefone, String email, String passaporte) {
        super(nome, telefone, email);
        this.passaporte = passaporte;
    }

    public String getPassaporte() {
        return passaporte;
    }

    public void setPassaporte(String passaporte) {
        this.passaporte = passaporte;
    }

    @Override
    public String toString() {
        return "\nCliente Estrangeiro:" +
               "\nID: " + getId() +
               "\nNome: " + getNome() +
               "\nTelefone: " + getTelefone() +
               "\nEmail: " + getEmail() +
               "\nPassaporte: " + passaporte;
    }

}
