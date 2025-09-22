package com.ProjectManager.model;

import java.util.List;

public enum UserProfile {
    ADMINISTRADOR("Administrador", List.of("CREATE", "READ", "UPDATE", "DELETE")),
    GERENTE("Gerente", List.of("CREATE", "READ", "UPDATE")),
    COLABORADOR("Colaborador", List.of("READ"));

    private final String displayName;
    private final List<String> permissions; // Adicione este campo

    UserProfile(String displayName, List<String> permissions) { // Atualize o construtor
        this.displayName = displayName;
        this.permissions = permissions;
    }

    public boolean hasPermission(String action) {
        return permissions.contains(action);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canManageUser(UserProfile other) {
        if (this == ADMINISTRADOR) return true;
        if (this == GERENTE && other == COLABORADOR) return true;
        return false;
    }
}
