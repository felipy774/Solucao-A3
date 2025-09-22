package com.ProjectManager.repository;

import com.ProjectManager.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    private static UserRepository instance;
    private List<User> users;

    private UserRepository() {
        this.users = new ArrayList<>();
    }

    public void delete(User user) {
    users.remove(user);
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public void save(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
    }

    public Optional<User> findById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public Optional<User> findByCpf(String cpf) {
        return users.stream().filter(u -> u.getCpf().equals(cpf)).findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    public Optional<User> findByLogin(String login) {
        return users.stream().filter(u -> u.getLogin().equals(login)).findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}
