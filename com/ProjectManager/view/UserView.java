package com.ProjectManager.view;

import com.ProjectManager.model.User;
import com.ProjectManager.model.UserProfile;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;

import java.util.Optional;

public class UserView {
    private UserRepository userRepository;
    private AuthenticationService authService;

    public UserView() {
        this.userRepository = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE USUÁRIOS");
            System.out.println("1. 👤 Cadastrar Usuário");
            System.out.println("2. 📋 Listar Usuários");
            System.out.println("3. 🔍 Buscar Usuário por CPF");
            System.out.println("4. ❌ Remover Usuário");
            System.out.println("0. ⬅️  Voltar");
            System.out.println();

            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    cadastrarUsuario();
                    break;
                case 2:
                    listarUsuarios();
                    break;
                case 3:
                    buscarUsuarioPorCpf();
                    break;
                case 4:
                    removerUsuario();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    ConsoleUtils.mostrarMensagemErro("Opção inválida!");
            }

            if (!voltar) {
                ConsoleUtils.pausar();
                ConsoleUtils.limparTela();
            }
        }
    }

    public void cadastrarUsuario() {
        ConsoleUtils.mostrarTitulo("CADASTRO DE USUÁRIO");

        String nome = ConsoleUtils.lerTexto("Nome completo: ");
        String cpf = ConsoleUtils.lerTexto("CPF: ");
        String email = ConsoleUtils.lerTexto("E-mail: ");
        String cargo = ConsoleUtils.lerTexto("Cargo: ");
        String login = ConsoleUtils.lerTexto("Login: ");
        String senha = ConsoleUtils.lerTexto("Senha: ");

        System.out.println("Perfis disponíveis: ");
        for (UserProfile profile : UserProfile.values()) {
            System.out.println("- " + profile.name() + " (" + profile.getDisplayName() + ")");
        }

        UserProfile perfil = null;
        while (perfil == null) {
            String perfilStr = ConsoleUtils.lerTexto("Perfil: ").toUpperCase();
            for (UserProfile p : UserProfile.values()) {
                if (p.name().equals(perfilStr)) {
                    perfil = p;
                    break;
                }
            }
        }

        User user = new User(nome, cpf, email, cargo, login, senha, perfil);
        userRepository.save(user);

        ConsoleUtils.mostrarMensagemSucesso("Usuário cadastrado com sucesso!");
    }

    public void listarUsuarios() {
        ConsoleUtils.mostrarTitulo("LISTA DE USUÁRIOS");

        for (User u : userRepository.findAll()) {
            System.out.println(u);
        }
    }

    public void buscarUsuarioPorCpf() {
        ConsoleUtils.mostrarTitulo("BUSCAR USUÁRIO POR CPF");

        String cpf = ConsoleUtils.lerTexto("Digite o CPF: ");
        Optional<User> userOpt = userRepository.findByCpf(cpf);
        User user = userOpt.orElse(null);

        if (user != null) {
            System.out.println(user);
        } else {
            ConsoleUtils.mostrarMensagemErro("Usuário não encontrado!");
        }
    }

    public void removerUsuario() {
        ConsoleUtils.mostrarTitulo("REMOVER USUÁRIO");

        String cpf = ConsoleUtils.lerTexto("Digite o CPF do usuário a remover: ");
        Optional<User> userOpt = userRepository.findByCpf(cpf);
        User user = userOpt.orElse(null);

        if (user != null) {
            userRepository.delete(user);
            ConsoleUtils.mostrarMensagemSucesso("Usuário removido com sucesso!");
        } else {
            ConsoleUtils.mostrarMensagemErro("Usuário não encontrado!");
        }
    }

    public void fazerLogin() {
        ConsoleUtils.mostrarTitulo("LOGIN DE USUÁRIO");

        String login = ConsoleUtils.lerTexto("Login: ");
        String senha = ConsoleUtils.lerTexto("Senha: ");

        if (authService.login(login, senha)) {
            ConsoleUtils.mostrarMensagemSucesso("Login realizado com sucesso!");
        } else {
            ConsoleUtils.mostrarMensagemErro("Login ou senha inválidos!");
        }
    }

    public void criarAdminPadrao() {
        if (userRepository.findAll().isEmpty()) {
            User admin = new User(
                "Administrador Padrão",
                "00000000000",
                "admin@sistema.com",
                "Administrador",
                "admin",
                "admin",
                UserProfile.ADMINISTRADOR
            );
            userRepository.save(admin);
            ConsoleUtils.mostrarMensagemSucesso("Usuário administrador padrão criado!");
        }
    }
}