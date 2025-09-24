package com.ProjectManager.model;

import com.ProjectManager.util.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private LocalDate prazo;
    private String id;
    private String nome;
    private String descricao;
    private ProjectStatus status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String teamId;
    private List<String> taskIds;
    private String gerenteId; 

    public Project(String nome, String descricao) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.status = ProjectStatus.PLANEJADO;
        this.dataInicio = LocalDateTime.now();
        this.taskIds = new ArrayList<>();
    }

    public Project(String nome, String descricao, LocalDate prazo) {
        this.id = java.util.UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
        this.status = ProjectStatus.PLANEJADO;
        this.dataInicio = LocalDateTime.now();
        this.taskIds = new ArrayList<>();
    }

    public Project(String nome, String descricao, LocalDate prazo, String gerenteId) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
        this.gerenteId = gerenteId;
        this.status = ProjectStatus.PLANEJAMENTO;
        this.dataInicio = LocalDateTime.now();
        this.teamId = null; // Inicialmente sem equipe
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void addTask(String taskId) { 
        taskIds.add(taskId); 
    }

    public boolean isCanceled() { 
        return status == ProjectStatus.CANCELADO; 
    }

    public boolean hasTeam() {
        return teamId != null && !teamId.trim().isEmpty();
    }


    // Getters e Setters existentes
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }
    public List<String> getTaskIds() { return taskIds; }

    // NOVOS GETTERS/SETTERS - Adicionar estes
    public LocalDate getPrazo() { 
        return prazo; 
    }
    
    public void setPrazo(LocalDate prazo) { 
        this.prazo = prazo; 
    }
    
    public String getGerenteId() { 
        return gerenteId; 
    }
    
    public void setGerenteId(String gerenteId) { 
        this.gerenteId = gerenteId; 
    }

    public void setId(String id) { 
        this.id = id; 
    }
}