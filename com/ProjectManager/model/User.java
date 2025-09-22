package com.ProjectManager.model;

public class User {
    private String id;
    private boolean ativo;
    private String nomeCompleto;
    private String cpf;
    private String email;
    private String cargo;
    private String login;
    private String senha;
    private UserProfile perfil;

    public User(String nomeCompleto, String cpf, String email, String cargo, String login, String senha, UserProfile perfil) {
        this.nomeCompleto = nomeCompleto;
        this.cpf = cpf;
        this.email = email;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public boolean isAtivo() {
    return ativo;
}

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getId() { // Adicione este método
    return id;
}

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UserProfile getPerfil() {
        return perfil;
    }

    public void setPerfil(UserProfile perfil) {
        this.perfil = perfil;
    }

    @Override
    public String toString() {
        return nomeCompleto + " (" + perfil.getDisplayName() + ")";
    }
}
