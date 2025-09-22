package com.ProjectManager.view;

import java.util.List;
import java.util.Optional;

import com.ProjectManager.model.Team;
import com.ProjectManager.model.User;
import com.ProjectManager.repository.TeamRepository;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;

public class TeamView {
    private TeamRepository teamRepo;
    private UserRepository userRepo;
    private AuthenticationService authService;

    public TeamView() {
        this.teamRepo = TeamRepository.getInstance();
        this.userRepo = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE EQUIPES");
            System.out.println("1. Criar equipe");
            System.out.println("2. Listar equipes");
            System.out.println("3. Adicionar membro");
            System.out.println("4. Remover membro");
            System.out.println("0. Voltar");
            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    criarEquipe();
                    break;
                case 2:
                    listarEquipes();
                    break;
                case 3:
                    adicionarMembro();
                    break;
                case 4:
                    removerMembro();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    // Nenhuma mensagem de erro, apenas ignora
            }
        }
    }

    private void criarEquipe() {
        String nome = ConsoleUtils.lerTexto("Nome da equipe: ");
        Team equipe = new Team(nome);
        teamRepo.save(equipe);
        ConsoleUtils.mostrarMensagemSucesso("Equipe criada com sucesso!");
    }

    private void listarEquipes() {
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            return;
        }
        ConsoleUtils.mostrarTitulo("LISTA DE EQUIPES");
        for (Team e : equipes) {
            System.out.println("Equipe: " + e.getNome());
            if (e.getMembros().isEmpty()) {
                System.out.println("  (Sem membros)");
            } else {
                System.out.println("  Membros:");
                for (User u : e.getMembros()) {
                    System.out.println("   - " + u.getNomeCompleto() + " (" + u.getPerfil().getDisplayName() + ")");
                }
            }
            System.out.println();
        }
    }

    private void adicionarMembro() {
        listarEquipes();
        String nomeEquipe = ConsoleUtils.lerTexto("Nome da equipe: ");
        Team equipe = teamRepo.findByName(nomeEquipe);
        if (equipe == null) {
            return;
        }

        String login = ConsoleUtils.lerTexto("Login do usuário para adicionar: ");
        Optional<User> usuarioOpt = userRepo.findByLogin(login);
        if (usuarioOpt.isEmpty()) {
            return;
        }
        User usuario = usuarioOpt.get();

        equipe.adicionarMembro(usuario);
        ConsoleUtils.mostrarMensagemSucesso("Membro adicionado à equipe!");
    }

    private void removerMembro() {
        listarEquipes();
        String nomeEquipe = ConsoleUtils.lerTexto("Nome da equipe: ");
        Team equipe = teamRepo.findByName(nomeEquipe);
        if (equipe == null) {
            return;
        }

        String login = ConsoleUtils.lerTexto("Login do usuário para remover: ");
        Optional<User> usuarioOpt = userRepo.findByLogin(login);
        if (usuarioOpt.isEmpty()) {
            return;
        }
        User usuario = usuarioOpt.get();

        equipe.removerMembro(usuario);
        ConsoleUtils.mostrarMensagemSucesso("Membro removido da equipe!");
    }
}