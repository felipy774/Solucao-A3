package com.ProjectManager.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma equipe dentro do sistema.
 */
public class Team {
    private List<String> memberIds;
    private String id;
    private String name;
    private String description;
    private List<User> members;

    public Team(String name, String description) {
        this.name = name;
        this.description = description;
        this.members = new ArrayList<>();
    }

    public Team(String name) {
        this.name = name;
        this.members = new ArrayList<>();
    }

    private List<User> membros; // Add this field if you want to store User objects

public List<User> getMembros() {
    return membros;
}

private String nome; // Add this field if missing

public String getNome() {
    return nome;
}

public void adicionarMembro(User usuario) {
    if (membros == null) {
        membros = new ArrayList<>();
    }
    membros.add(usuario);
}

public void removerMembro(User usuario) {
    if (membros != null) {
        membros.remove(usuario);
    }
}

    public String getName() {
        return name;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMembers() {
        return members;
    }

    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
        }
    }

    public void removeMember(User user) {
        members.remove(user);
    }

    @Override
    public String toString() {
        return "Equipe: " + name + " | Descrição: " + description + " | Membros: " + members.size();
    }
}