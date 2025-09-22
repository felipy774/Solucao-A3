package com.ProjectManager.repository;

import com.ProjectManager.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepository {
    private static TaskRepository instance;
    private List<Task> tasks;

    private TaskRepository() {
        this.tasks = new ArrayList<>();
    }

    public static TaskRepository getInstance() {
        if (instance == null) {
            instance = new TaskRepository();
        }
        return instance;
    }

    public void delete(String id) {
    tasks.removeIf(task -> task.getId().equals(id));
}

    public void save(Task task) {
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        tasks.add(task);
    }

    public Optional<Task> findById(String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }
}
