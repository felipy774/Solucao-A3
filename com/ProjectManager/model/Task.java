package com.ProjectManager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private String id;
    private String titulo;
    private String descricao;
    private String projectId;
    private String teamId;
    private String responsavelId;
    private TaskStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataConclusao;
    private String nome;
    private LocalDate prazo;
    private String projetoId;

public Task(String nome, String descricao, LocalDate prazo, String projetoId) {
    this.id = java.util.UUID.randomUUID().toString();
    this.nome = nome;
    this.descricao = descricao;
    this.prazo = prazo;
    this.projetoId = projetoId;
    this.status = TaskStatus.PENDENTE;
    this.dataCriacao = LocalDateTime.now();
}

public void validateRequiredFields() {
    // Simples: titulo e descricao obrigatórios
}

public String getProjetoId() {
    return projetoId;
}

public LocalDate getPrazo() {
    return prazo;
}

public void setStatus(TaskStatus status) {
    this.status = status;
}

private String usuarioId; // Add this field if it doesn't exist

public void setUsuarioId(String usuarioId) {
    this.usuarioId = usuarioId;
}

public boolean assignToUser(String userId) {
    if (status != TaskStatus.PENDENTE) return false;
    this.status = TaskStatus.EM_ANDAMENTO;
    this.responsavelId = userId;
    return true;
}

    public boolean markAsCompleted(String userId) {
        if (status != TaskStatus.EM_ANDAMENTO) return false;
        this.status = TaskStatus.CONCLUIDO;
        this.dataConclusao = LocalDateTime.now();
        return true;
    }

    // Getters e Setters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getProjectId() { return projectId; }
    public String getTeamId() { return teamId; }
    public String getResponsavelId() { return responsavelId; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public LocalDateTime getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDateTime dataVencimento) { this.dataVencimento = dataVencimento; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
}
