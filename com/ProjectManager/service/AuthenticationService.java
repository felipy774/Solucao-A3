package com.ProjectManager.service;

import com.ProjectManager.model.User;
import com.ProjectManager.model.UserProfile;
import com.ProjectManager.repository.UserRepository;

import java.util.Optional;

public class AuthenticationService {
    private static AuthenticationService instance;
    private User currentUser;

    private UserRepository userRepo;

    private AuthenticationService() {
        this.userRepo = UserRepository.getInstance();
    }

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean login(String login, String senha) {
        Optional<User> opt = userRepo.findByLogin(login);
        if (opt.isPresent()) {
            User user = opt.get();
            if (user.isAtivo() && user.getSenha().equals(senha)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasPermission(String action) {
        if (!isLoggedIn()) return false;
        UserProfile perfil = currentUser.getPerfil();
        return perfil.hasPermission(action);
    }
}
