package com.ProjectManager.model;

public enum TaskStatus {
    PENDENTE,
    EM_ANDAMENTO,
    CONCLUIDO;

    public String getDisplayName() {
        return this.name().replace("_", " ");
    }
}
