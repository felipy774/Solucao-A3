package com.ProjectManager.model;

import com.ProjectManager.util.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {
    private String id;
    private String nome;
    private String descricao;
    private String projetoId;
    private String responsavelId;
    private TaskStatus status;
    private LocalDateTime dataCriacao;
    private LocalDate prazo;
    private LocalDateTime dataConclusao;

    // Construtor principal para criar nova tarefa
    public Task(String nome, String descricao, LocalDate prazo, String projetoId) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
        this.projetoId = projetoId;
        this.status = TaskStatus.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    // Construtor para carregar tarefa do arquivo (com ID e status conhecidos)
    public Task(String id, String nome, String descricao, LocalDate prazo, TaskStatus status, String projetoId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
        this.status = status;
        this.projetoId = projetoId;
        this.dataCriacao = LocalDateTime.now();
    }

    // Validação de campos obrigatórios
    public void validateRequiredFields() {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da tarefa é obrigatório!");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição da tarefa é obrigatória!");
        }
        if (prazo == null) {
            throw new IllegalArgumentException("Prazo da tarefa é obrigatório!");
        }
    }

    // Métodos de negócio
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

    public boolean isAtrasada() {
        return prazo.isBefore(LocalDate.now()) && status != TaskStatus.CONCLUIDO;
    }

    // Getters essenciais para o Repository
    public String getId() { 
        return id; 
    }

    public String getNome() { 
        return nome; 
    }

    public String getDescricao() { 
        return descricao; 
    }

    public LocalDate getPrazo() { 
        return prazo; 
    }

    public String getProjetoId() { 
        return projetoId; 
    }

    public TaskStatus getStatus() { 
        return status; 
    }

    public String getResponsavelId() { 
        return responsavelId; 
    }

    public LocalDateTime getDataCriacao() { 
        return dataCriacao; 
    }

    public LocalDateTime getDataConclusao() { 
        return dataConclusao; 
    }

    // Setters necessários para carregar do arquivo
    public void setId(String id) { 
        this.id = id; 
    }

    public void setNome(String nome) { 
        this.nome = nome; 
    }

    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public void setPrazo(LocalDate prazo) { 
        this.prazo = prazo; 
    }

    public void setStatus(TaskStatus status) { 
        this.status = status; 
    }

    public void setProjetoId(String projetoId) { 
        this.projetoId = projetoId; 
    }

    public void setResponsavelId(String responsavelId) { 
        this.responsavelId = responsavelId; 
    }

    // ToString para exibição
    @Override
    public String toString() {
        return String.format("Tarefa: %s | Prazo: %s | Status: %s | Projeto: %s", 
                           nome, 
                           DateUtils.formatarData(prazo), 
                           status, 
                           projetoId != null ? projetoId : "N/A");
    }

    // Equals e hashCode baseados no ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return id != null && id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}