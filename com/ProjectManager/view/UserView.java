package com.ProjectManager.view;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import com.ProjectManager.model.User;
import com.ProjectManager.model.UserProfile;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;

public class UserView {
    private UserRepository userRepository;
    private AuthenticationService authService;
    private Scanner scanner;

    public UserView() {
        this.userRepository = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
        this.scanner = new Scanner(System.in);
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

            int opcao = lerInt("Escolha uma opção: ");

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
                    System.out.println("Opção inválida! Tente novamente.");
            }

            if (!voltar) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
                limparTela();
            }
        }
    }

    public void cadastrarUsuario() {
        ConsoleUtils.mostrarTitulo("CADASTRO DE USUÁRIO");

        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();
        
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        
        System.out.print("E-mail: ");
        String email = scanner.nextLine();
        
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        
        System.out.print("Login: ");
        String login = scanner.nextLine();
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.println("\nPerfis disponíveis:");
        UserProfile[] perfis = UserProfile.values();
        for (int i = 0; i < perfis.length; i++) {
            System.out.println((i + 1) + ". " + perfis[i].name() + " - " + perfis[i].getDisplayName());
        }

        int opcaoPerfil = 0;
        boolean perfilValido = false;
        
        while (!perfilValido) {
            opcaoPerfil = lerInt("Escolha um perfil (1-" + perfis.length + "): ");
            
            if (opcaoPerfil >= 1 && opcaoPerfil <= perfis.length) {
                perfilValido = true;
            } else {
                System.out.println("Opção inválida! Tente novamente.");
            }
        }

        UserProfile perfil = perfis[opcaoPerfil - 1];

        try {
            User user = new User(nome, cpf, email, cargo, login, senha, perfil);
            userRepository.save(user);
            ConsoleUtils.mostrarMensagemSucesso("Usuário cadastrado com sucesso!");
            System.out.println("Perfil selecionado: " + perfil.getDisplayName());
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    public void listarUsuarios() {
        ConsoleUtils.mostrarTitulo("LISTA DE USUÁRIOS");

        List<User> usuarios = userRepository.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        for (User u : usuarios) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Nome: " + u.getNomeCompleto());
            System.out.println("CPF: " + u.getCpf());
            System.out.println("Email: " + u.getEmail());
            System.out.println("Login: " + u.getLogin());
            System.out.println("Perfil: " + u.getPerfil().getDisplayName());
            System.out.println("Status: " + (u.isAtivo() ? "Ativo" : "Inativo"));
            System.out.println();
        }
    }

    public void buscarUsuarioPorCpf() {
        ConsoleUtils.mostrarTitulo("BUSCAR USUÁRIO POR CPF");

        System.out.print("Digite o CPF: ");
        String cpf = scanner.nextLine();
        
        Optional<User> userOpt = userRepository.findByCpf(cpf);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Nome: " + user.getNomeCompleto());
            System.out.println("CPF: " + user.getCpf());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Login: " + user.getLogin());
            System.out.println("Perfil: " + user.getPerfil().getDisplayName());
            System.out.println("Status: " + (user.isAtivo() ? "Ativo" : "Inativo"));
        } else {
            System.out.println("Usuário não encontrado!");
        }
    }

    public void removerUsuario() {
        ConsoleUtils.mostrarTitulo("REMOVER USUÁRIO");

        System.out.print("Digite o CPF do usuário a remover: ");
        String cpf = scanner.nextLine();
        
        Optional<User> userOpt = userRepository.findByCpf(cpf);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userRepository.delete(user);
            ConsoleUtils.mostrarMensagemSucesso("Usuário removido com sucesso!");
        } else {
            System.out.println("Usuário não encontrado!");
        }
    }

    public void fazerLogin() {
        ConsoleUtils.mostrarTitulo("LOGIN DE USUÁRIO");

        System.out.print("Login: ");
        String login = scanner.nextLine();
        
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        if (authService.login(login, senha)) {
            ConsoleUtils.mostrarMensagemSucesso("Login realizado com sucesso!");
        } else {
            System.out.println("Login ou senha inválidos!");
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

    // Métodos auxiliares
    private int lerInt(String prompt) {
        int valor = 0;
        boolean valido = false;
        
        while (!valido) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                valor = Integer.parseInt(input);
                valido = true;
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            }
        }
        
        return valor;
    }

    private void limparTela() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[2J\033[H");
            }
        } catch (Exception e) {
            // Se não conseguir limpar, apenas pula linhas
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}