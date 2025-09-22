package com.ProjectManager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private LocalDate prazo;
    private String id;
    private String nome;
    private String descricao;
    private ProjectStatus status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private List<String> taskIds;

    public Project(String nome, String descricao) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.status = ProjectStatus.PLANEJADO;
        this.dataInicio = LocalDateTime.now();
        this.taskIds = new ArrayList<>();
    }

    public void setDescricao(String descricao) {
    this.descricao = descricao;
}

    public void setNome(String nome) {
    this.nome = nome;
}

    public Project(String nome, String descricao, LocalDate prazo) {
    this.nome = nome;
    this.descricao = descricao;
    this.prazo = prazo;
}

    public void addTask(String taskId) { taskIds.add(taskId); }

    public boolean isCanceled() { return status == ProjectStatus.CANCELADO; }

    // Getters e Setters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public List<String> getTaskIds() { return taskIds; }
}
