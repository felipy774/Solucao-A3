package com.ProjectManager.model;

import java.util.Objects;

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

    // Construtor corrigido
    public User(String nomeCompleto, String cpf, String email, String cargo, String login, String senha, UserProfile perfil) {
        this.id = java.util.UUID.randomUUID().toString(); // Gera ID único
        this.ativo = true; // Define como ativo por padrão
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : "";
        this.cpf = cpf != null ? cpf.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.cargo = cargo != null ? cargo.trim() : "";
        this.login = login != null ? login.trim() : "";
        this.senha = senha != null ? senha : "";
        this.perfil = perfil != null ? perfil : UserProfile.COLABORADOR;
    }

    // Construtor adicional com ID personalizado (para casos especiais)
    public User(String id, String nomeCompleto, String cpf, String email, String cargo, String login, String senha, UserProfile perfil, boolean ativo) {
        this.id = id != null ? id : java.util.UUID.randomUUID().toString();
        this.ativo = ativo;
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : "";
        this.cpf = cpf != null ? cpf.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.cargo = cargo != null ? cargo.trim() : "";
        this.login = login != null ? login.trim() : "";
        this.senha = senha != null ? senha : "";
        this.perfil = perfil != null ? perfil : UserProfile.COLABORADOR;
    }

    // Getters
    public String getId() {
        return id;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }

    public String getLogin() {
        return login;
    }

    public String getSenha() {
        return senha;
    }

    public UserProfile getPerfil() {
        return perfil;
    }

    // Setters com validação
    public void setId(String id) {
        this.id = id != null ? id : java.util.UUID.randomUUID().toString();
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto != null ? nomeCompleto.trim() : "";
    }

    public void setCpf(String cpf) {
        this.cpf = cpf != null ? cpf.trim() : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public void setCargo(String cargo) {
        this.cargo = cargo != null ? cargo.trim() : "";
    }

    public void setLogin(String login) {
        this.login = login != null ? login.trim() : "";
    }

    public void setSenha(String senha) {
        this.senha = senha != null ? senha : "";
    }

    public void setPerfil(UserProfile perfil) {
        this.perfil = perfil != null ? perfil : UserProfile.COLABORADOR;
    }

    // Métodos de validação
    public boolean isValido() {
        return id != null && !id.isEmpty() &&
               nomeCompleto != null && !nomeCompleto.trim().isEmpty() &&
               cpf != null && !cpf.trim().isEmpty() &&
               login != null && !login.trim().isEmpty() &&
               senha != null && !senha.isEmpty() &&
               perfil != null;
    }

    public boolean podeAcessar(String funcionalidade) {
        if (!ativo) return false;
        
        switch (perfil) {
            case ADMINISTRADOR:
                return true; // Admin pode tudo
            case GERENTE:
                return !funcionalidade.equals("GERENCIAR_USUARIOS") || 
                       funcionalidade.equals("CRIAR_PROJETO") || 
                       funcionalidade.equals("GERENCIAR_EQUIPES");
            case COLABORADOR:
                return funcionalidade.equals("VER_PROJETOS") || 
                       funcionalidade.equals("VER_TAREFAS");
            default:
                return false;
        }
    }

    // Método para alterar senha com validação
    public boolean alterarSenha(String senhaAtual, String novaSenha) {
        if (senha != null && senha.equals(senhaAtual)) {
            this.senha = novaSenha != null ? novaSenha : "";
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Usuário: %s | CPF: %s | Email: %s | Perfil: %s | Status: %s", 
                           nomeCompleto, 
                           cpf, 
                           email, 
                           perfil != null ? perfil.getDisplayName() : "N/A", 
                           ativo ? "Ativo" : "Inativo");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) && Objects.equals(login, user.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login);
    }

    // Método para clonar usuário (útil para edição)
    public User clone() {
        return new User(this.id, this.nomeCompleto, this.cpf, this.email, 
                       this.cargo, this.login, this.senha, this.perfil, this.ativo);
    }

    // Método estático para criar usuário administrador padrão
    public static User criarAdminPadrao() {
        return new User(
            "Administrador do Sistema",
            "00000000000",
            "admin@sistema.com",
            "Administrador",
            "admin",
            "admin123",
            UserProfile.ADMINISTRADOR
        );
    }
}