package com.ProjectManager.model;

public enum ProjectStatus {
    PLANEJAMENTO,
    PLANEJADO,
    EM_ANDAMENTO,
    CANCELADO,
    CONCLUIDO;

    public String getDisplayName() {
        switch (this) {
            case PLANEJAMENTO:
                return "Em Planejamento";
            case PLANEJADO:
                return "Planejado";
            case EM_ANDAMENTO:
                return "Em Andamento";
            case CANCELADO:
                return "Cancelado";
            case CONCLUIDO:
                return "Concluído";
            default:
                return this.name();
        }
    }

    public String getDescription() {
        switch (this) {
            case PLANEJAMENTO:
                return "Projeto está sendo planejado e definido";
            case PLANEJADO:
                return "Projeto planejado, aguardando início";
            case EM_ANDAMENTO:
                return "Projeto em execução";
            case CANCELADO:
                return "Projeto foi cancelado";
            case CONCLUIDO:
                return "Projeto foi concluído com sucesso";
            default:
                return "Status desconhecido";
        }
    }
}