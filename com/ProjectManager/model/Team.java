package com.ProjectManager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Representa uma equipe dentro do sistema.
 */
public class Team {
    private String id;
    private String name;
    private String nome; // Para compatibilidade
    private String description;
    private List<User> members;
    private List<User> membros; // Para compatibilidade
    private List<String> memberIds;

    // CONSTRUTOR PRINCIPAL
    public Team(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.nome = name; // Para compatibilidade
        this.description = description;
        this.members = new ArrayList<>();
        this.membros = new ArrayList<>(); // Para compatibilidade
        this.memberIds = new ArrayList<>();
    }

    // CONSTRUTOR ALTERNATIVO
    public Team(String name) {
        this(name, "");
    }

    // GETTERS E SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.nome = name; // Manter sincronizado
    }

    public String getNome() {
        return name; // Usar o campo principal
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMembers() {
        return members != null ? members : new ArrayList<>();
    }

    public List<User> getMembros() {
        return getMembers(); // Usar o método principal
    }

    public List<String> getMemberIds() {
        return memberIds != null ? memberIds : new ArrayList<>();
    }

    // MÉTODOS PARA GERENCIAR MEMBROS
    public void addMember(User user) {
        if (user != null && !members.contains(user)) {
            members.add(user);
            membros.add(user); // Manter sincronizado
            
            // Adicionar ID se não existir
            if (!memberIds.contains(user.getId())) {
                memberIds.add(user.getId());
            }
        }
    }

    public void adicionarMembro(User usuario) {
        addMember(usuario); // Usar método principal
    }

    public void removeMember(User user) {
        if (user != null) {
            members.remove(user);
            membros.remove(user); // Manter sincronizado
            memberIds.remove(user.getId());
        }
    }

    public void removerMembro(User usuario) {
        removeMember(usuario); // Usar método principal
    }

    public boolean hasMember(String userId) {
        return memberIds.contains(userId);
    }

    public int getMemberCount() {
        return members.size();
    }

    // MÉTODO PARA VALIDAÇÃO
    public void validateRequiredFields() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da equipe é obrigatório!");
        }
        if (id == null || id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    @Override
    public String toString() {
        return String.format("Equipe: %s | Descrição: %s | Membros: %d", 
                           name, 
                           description != null ? description : "Sem descrição", 
                           getMemberCount());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Team team = (Team) obj;
        return id != null && id.equals(team.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}