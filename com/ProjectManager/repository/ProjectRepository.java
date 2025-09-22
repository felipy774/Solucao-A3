package com.ProjectManager.repository;

import com.ProjectManager.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepository {
    private static ProjectRepository instance;
    private List<Project> projects;

    private ProjectRepository() {
        this.projects = new ArrayList<>();
    }

    public void delete(String id) {
    projects.removeIf(project -> project.getId().equals(id));
}

    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    public void save(Project project) {
        projects.removeIf(p -> p.getId().equals(project.getId()));
        projects.add(project);
    }

    public Optional<Project> findById(String id) {
        return projects.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Project> findAll() {
        return new ArrayList<>(projects);
    }
}
