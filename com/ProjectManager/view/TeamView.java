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
                    System.out.println("Opção inválida! Tente novamente.");
            }

            if (!voltar) {
                ConsoleUtils.pausar();
                ConsoleUtils.limparTela();
            }
        }
    }

    private void criarEquipe() {
        ConsoleUtils.mostrarTitulo("CRIAR EQUIPE");
        String nome = ConsoleUtils.lerTexto("Nome da equipe: ");
        
        // Verifica se já existe uma equipe com esse nome
        if (teamRepo.findByName(nome) != null) {
            System.out.println("Já existe uma equipe com esse nome!");
            return;
        }
        
        Team equipe = new Team(nome);
        teamRepo.save(equipe);
        ConsoleUtils.mostrarMensagemSucesso("Equipe criada com sucesso!");
    }

    private void listarEquipes() {
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            System.out.println("Nenhuma equipe cadastrada.");
            return;
        }
        
        ConsoleUtils.mostrarTitulo("LISTA DE EQUIPES");
        for (Team e : equipes) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Equipe: " + e.getNome());
            if (e.getMembros() == null || e.getMembros().isEmpty()) {
                System.out.println("  (Sem membros)");
            } else {
                System.out.println("  Membros (" + e.getMembros().size() + "):");
                for (User u : e.getMembros()) {
                    System.out.println("   - " + u.getNomeCompleto() + " (" + u.getPerfil().getDisplayName() + ")");
                }
            }
            System.out.println();
        }
    }

    private void adicionarMembro() {
        ConsoleUtils.mostrarTitulo("ADICIONAR MEMBRO À EQUIPE");
        
        // Lista equipes disponíveis
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            System.out.println("Nenhuma equipe cadastrada. Crie uma equipe primeiro.");
            return;
        }
        
        System.out.println("Equipes disponíveis:");
        for (Team t : equipes) {
            System.out.println("- " + t.getNome());
        }
        System.out.println();
        
        String nomeEquipe = ConsoleUtils.lerTexto("Nome da equipe: ");
        Team equipe = teamRepo.findByName(nomeEquipe);
        if (equipe == null) {
            System.out.println("Equipe não encontrada!");
            return;
        }

        // Lista usuários disponíveis
        List<User> usuarios = userRepo.findAll();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }
        
        System.out.println("Usuários disponíveis:");
        for (User u : usuarios) {
            System.out.println("- " + u.getLogin() + " (" + u.getNomeCompleto() + ")");
        }
        System.out.println();

        String login = ConsoleUtils.lerTexto("Login do usuário para adicionar: ");
        Optional<User> usuarioOpt = userRepo.findByLogin(login);
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado!");
            return;
        }
        User usuario = usuarioOpt.get();

        // Verifica se o usuário já está na equipe
        if (equipe.getMembros() != null && equipe.getMembros().contains(usuario)) {
            System.out.println("Usuário já está na equipe!");
            return;
        }

        equipe.adicionarMembro(usuario);
        ConsoleUtils.mostrarMensagemSucesso("Membro adicionado à equipe!");
    }

    private void removerMembro() {
        ConsoleUtils.mostrarTitulo("REMOVER MEMBRO DA EQUIPE");
        
        // Lista equipes com membros
        List<Team> equipes = teamRepo.findAll();
        List<Team> equipesComMembros = equipes.stream()
            .filter(t -> t.getMembros() != null && !t.getMembros().isEmpty())
            .toList();
            
        if (equipesComMembros.isEmpty()) {
            System.out.println("Nenhuma equipe possui membros para remover.");
            return;
        }
        
        System.out.println("Equipes com membros:");
        for (Team t : equipesComMembros) {
            System.out.println("- " + t.getNome() + " (" + t.getMembros().size() + " membros)");
        }
        System.out.println();
        
        String nomeEquipe = ConsoleUtils.lerTexto("Nome da equipe: ");
        Team equipe = teamRepo.findByName(nomeEquipe);
        if (equipe == null) {
            System.out.println("Equipe não encontrada!");
            return;
        }

        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) {
            System.out.println("Esta equipe não possui membros!");
            return;
        }

        // Lista membros da equipe
        System.out.println("Membros da equipe " + equipe.getNome() + ":");
        for (User u : equipe.getMembros()) {
            System.out.println("- " + u.getLogin() + " (" + u.getNomeCompleto() + ")");
        }
        System.out.println();

        String login = ConsoleUtils.lerTexto("Login do usuário para remover: ");
        Optional<User> usuarioOpt = userRepo.findByLogin(login);
        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado!");
            return;
        }
        User usuario = usuarioOpt.get();

        // Verifica se o usuário está na equipe
        if (!equipe.getMembros().contains(usuario)) {
            System.out.println("Usuário não está na equipe!");
            return;
        }

        equipe.removerMembro(usuario);
        ConsoleUtils.mostrarMensagemSucesso("Membro removido da equipe!");
    }
}