package com.ProjectManager.view;

import com.ProjectManager.model.Project;
import com.ProjectManager.model.ProjectStatus;
import com.ProjectManager.model.Team;
import com.ProjectManager.repository.ProjectRepository;
import com.ProjectManager.repository.TeamRepository;
import com.ProjectManager.service.AuthenticationService;
import com.ProjectManager.service.LogService;
import com.ProjectManager.util.DateUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjectView {
    private ProjectRepository projectRepo = ProjectRepository.getInstance();
    private TeamRepository teamRepo = TeamRepository.getInstance();
    private AuthenticationService authService = AuthenticationService.getInstance();
    private LogService logService = LogService.getInstance();

    public ProjectView() {
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.limparTela();
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE PROJETOS");
            System.out.println("1. ➕ Criar Projeto");
            System.out.println("2. 📋 Listar Projetos");
            System.out.println("3. ✏️ Editar Projeto");
            System.out.println("4. 👥 Vincular Equipe");
            System.out.println("5. ❌ Desvincular Equipe");
            System.out.println("6. 📊 Alterar Status");
            System.out.println("7. 🗑️ Excluir Projeto");
            System.out.println("0. ⬅️ Voltar ao Menu Principal");
            
            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");
            
            switch (opcao) {
                case 0:
                    voltar = true;
                    break;
                case 1:
                    this.criarProjeto();
                    break;
                case 2:
                    this.listarProjetos();
                    break;
                case 3:
                    this.editarProjeto();
                    break;
                case 4:
                    this.vincularEquipe();
                    break;
                case 5:
                    this.desvincularEquipe();
                    break;
                case 6:
                    this.alterarStatusMenu();
                    break;
                case 7:
                    this.excluirProjeto();
                    break;
                default:
                    ConsoleUtils.mostrarMensagemErro("Opção inválida!");
            }

            if (!voltar) {
                ConsoleUtils.pausar();
            }
        }
    }

    private void criarProjeto() {
        ConsoleUtils.mostrarTitulo("CRIAR NOVO PROJETO");
        
        String nome = ConsoleUtils.lerString("Nome do projeto: ");
        if (nome.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Nome é obrigatório!");
            return;
        }

        String descricao = ConsoleUtils.lerString("Descrição: ");
        if (descricao.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Descrição é obrigatória!");
            return;
        }
        
        LocalDate prazo = DateUtils.lerData("Prazo do projeto");
        if (prazo == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }
        
        String gerenteId = ConsoleUtils.lerString("ID do gerente (opcional): ");

        try {
            Project projeto = new Project(nome, descricao, prazo, gerenteId);
            this.projectRepo.save(projeto);
            
            if (this.authService.getCurrentUser() != null) {
                this.logService.log(this.authService.getCurrentUser().getId(), "CREATE_PROJECT", 
                                   projeto.getId(), "Projeto criado: " + nome);
            }
            
            ConsoleUtils.mostrarMensagemSucesso("Projeto criado com sucesso!");
            System.out.println("ID do projeto: " + projeto.getId().substring(0, 8) + "...");
            System.out.println("Prazo: " + DateUtils.formatarData(prazo));
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao criar projeto: " + e.getMessage());
        }
    }

    private void listarProjetos() {
        ConsoleUtils.mostrarTitulo("LISTA DE PROJETOS");
        
        List<Project> projetos = this.projectRepo.findAll();
        if (projetos.isEmpty()) {
            System.out.println("Nenhum projeto cadastrado.");
            return;
        }

        System.out.println("=== PROJETOS CADASTRADOS ===");
        for (int i = 0; i < projetos.size(); i++) {
            Project projeto = projetos.get(i);
            System.out.printf("%d. [%s] %s\n", 
                    (i + 1),
                    projeto.getId().substring(0, 8), 
                    projeto.getNome());
            System.out.printf("   Descrição: %s\n", projeto.getDescricao());
            System.out.printf("   Status: %s\n", projeto.getStatus().getDisplayName());
            
            if (projeto.getPrazo() != null) {
                System.out.printf("   Prazo: %s\n", DateUtils.formatarData(projeto.getPrazo()));
            }

            if (projeto.getGerenteId() != null && !projeto.getGerenteId().trim().isEmpty()) {
                System.out.printf("   Gerente: %s\n", projeto.getGerenteId());
            }

            if (projeto.hasTeam()) {
                Optional<Team> team = teamRepo.findById(projeto.getTeamId());
                if (team.isPresent()) {
                    System.out.printf("   👥 Equipe: %s (%d membros)\n", 
                                    team.get().getNome(),
                                    team.get().getMemberIds() != null ? team.get().getMemberIds().size() : 0);
                } else {
                    System.out.printf("   ⚠️ Equipe: ID não encontrado (%s)\n", 
                                    projeto.getTeamId().substring(0, 8));
                }
            } else {
                System.out.println("   👥 Equipe: Não vinculada");
            }
            
            System.out.println("   " + "-".repeat(40));
        }
    }

    private void editarProjeto() {
        ConsoleUtils.mostrarTitulo("EDITAR PROJETO");
        
        Project projeto = selecionarProjeto("SELECIONAR PROJETO PARA EDITAR");
        if (projeto == null) {
            return;
        }

        System.out.println("\n✅ PROJETO SELECIONADO:");
        System.out.println("Nome: " + projeto.getNome());
        System.out.println("Descrição: " + projeto.getDescricao());
        System.out.println("Status: " + projeto.getStatus().getDisplayName());
        if (projeto.getPrazo() != null) {
            System.out.println("Prazo: " + DateUtils.formatarData(projeto.getPrazo()));
        }
        
        String confirmacao = ConsoleUtils.lerString("\nConfirma este projeto? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.println("\n📝 EDIÇÃO DO PROJETO:");
        System.out.println("(Deixe em branco para manter o valor atual)");
        
        String novoNome = ConsoleUtils.lerString("Novo nome: ");
        String novaDescricao = ConsoleUtils.lerString("Nova descrição: ");

        if (!novoNome.trim().isEmpty()) {
            projeto.setNome(novoNome);
            System.out.println("✓ Nome atualizado!");
        }
        
        if (!novaDescricao.trim().isEmpty()) {
            projeto.setDescricao(novaDescricao);
            System.out.println("✓ Descrição atualizada!");
        }

        String alterarPrazo = ConsoleUtils.lerString("Deseja alterar o prazo? (s/n): ");
        if (alterarPrazo.equalsIgnoreCase("s")) {
            LocalDate novoPrazo = DateUtils.lerData("Novo prazo");
            if (novoPrazo != null) {
                projeto.setPrazo(novoPrazo);
                System.out.println("✓ Prazo atualizado!");
            }
        }

        try {
            projectRepo.save(projeto);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "EDIT_PROJECT", 
                              projeto.getId(), "Projeto editado: " + projeto.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Projeto atualizado com sucesso!");
            
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar: " + e.getMessage());
        }
    }

    private void vincularEquipe() {
        ConsoleUtils.mostrarTitulo("VINCULAR EQUIPE AO PROJETO");

        Project projeto = selecionarProjeto("SELECIONAR PROJETO");
        if (projeto == null) {
            return;
        }

        if (projeto.hasTeam()) {
            Optional<Team> equipeAtual = teamRepo.findById(projeto.getTeamId());
            String nomeEquipe = equipeAtual.isPresent() ? equipeAtual.get().getNome() : "Equipe não encontrada";
            
            System.out.println("⚠️ Este projeto já tem uma equipe vinculada: " + nomeEquipe);
            String confirmar = ConsoleUtils.lerString("Deseja trocar a equipe? (s/n): ");
            if (!confirmar.equalsIgnoreCase("s")) {
                System.out.println("Operação cancelada.");
                return;
            }
        }

        Team equipe = selecionarEquipe();
        if (equipe == null) {
            return;
        }

        System.out.println("\n📋 RESUMO DA VINCULAÇÃO:");
        System.out.println("Projeto: " + projeto.getNome());
        System.out.println("Equipe: " + equipe.getNome());
        System.out.println("Membros da equipe: " + (equipe.getMemberIds() != null ? equipe.getMemberIds().size() : 0));

        String confirmacao = ConsoleUtils.lerString("Confirma a vinculação? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        try {
            projeto.setTeamId(equipe.getId());
            projectRepo.save(projeto);

            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "LINK_TEAM", 
                              projeto.getId(), "Equipe vinculada: " + equipe.getNome());
            }

            ConsoleUtils.mostrarMensagemSucesso("✅ Equipe vinculada ao projeto com sucesso!");
            System.out.println("📋 RESUMO:");
            System.out.println("Projeto: " + projeto.getNome());
            System.out.println("Equipe: " + equipe.getNome());
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao vincular equipe: " + e.getMessage());
        }
    }

    private void desvincularEquipe() {
        ConsoleUtils.mostrarTitulo("DESVINCULAR EQUIPE DO PROJETO");

        List<Project> projetos = projectRepo.findAll();
        List<Project> projetosComEquipe = new java.util.ArrayList<>();
        
        for (Project p : projetos) {
            if (p.hasTeam()) {
                projetosComEquipe.add(p);
            }
        }

        if (projetosComEquipe.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhum projeto possui equipe vinculada.");
            return;
        }

        System.out.println("=== PROJETOS COM EQUIPE VINCULADA ===");
        for (int i = 0; i < projetosComEquipe.size(); i++) {
            Project p = projetosComEquipe.get(i);
            Optional<Team> team = teamRepo.findById(p.getTeamId());
            String nomeEquipe = team.isPresent() ? team.get().getNome() : "Equipe não encontrada";
            
            System.out.printf("%d. %s\n", (i + 1), p.getNome());
            System.out.printf("   Equipe: %s\n", nomeEquipe);
            System.out.println("   " + "-".repeat(30));
        }

        String busca = ConsoleUtils.lerString("Digite o número ou nome do projeto: ");
        Project projeto = encontrarProjeto(busca, projetosComEquipe);

        if (projeto == null) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        Optional<Team> equipe = teamRepo.findById(projeto.getTeamId());
        String nomeEquipe = equipe.isPresent() ? equipe.get().getNome() : "Equipe não encontrada";

        System.out.println("\n⚠️ DESVINCULAÇÃO:");
        System.out.println("Projeto: " + projeto.getNome());
        System.out.println("Equipe atual: " + nomeEquipe);

        String confirmacao = ConsoleUtils.lerString("Confirma a desvinculação? (s/n): ");
        if (confirmacao.equalsIgnoreCase("s")) {
            projeto.setTeamId(null);
            projectRepo.save(projeto);

            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "UNLINK_TEAM", 
                              projeto.getId(), "Equipe desvinculada: " + nomeEquipe);
            }

            ConsoleUtils.mostrarMensagemSucesso("✅ Equipe desvinculada com sucesso!");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void alterarStatusMenu() {
        ConsoleUtils.mostrarTitulo("ALTERAR STATUS DO PROJETO");

        Project projeto = selecionarProjeto("SELECIONAR PROJETO");
        if (projeto == null) {
            return;
        }

        System.out.println("\n📊 STATUS ATUAL: " + projeto.getStatus().getDisplayName());
        System.out.println("Descrição: " + projeto.getStatus().getDescription());

        alterarStatusProjeto(projeto);

        try {
            projectRepo.save(projeto);

            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "CHANGE_STATUS", 
                              projeto.getId(), "Status alterado para: " + projeto.getStatus());
            }

            ConsoleUtils.mostrarMensagemSucesso("✅ Status atualizado com sucesso!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar status: " + e.getMessage());
        }
    }

    private Project selecionarProjeto(String titulo) {
        List<Project> projetos = projectRepo.findAll();
        if (projetos.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhum projeto cadastrado!");
            return null;
        }

        System.out.println("=== " + titulo + " ===");
        for (int i = 0; i < projetos.size(); i++) {
            Project p = projetos.get(i);
            System.out.printf("%d. %s\n", (i + 1), p.getNome());
            System.out.printf("   ID: %s | Status: %s\n", 
                    p.getId().substring(0, 8) + "...", p.getStatus().getDisplayName());
            
            if (p.hasTeam()) {
                Optional<Team> team = teamRepo.findById(p.getTeamId());
                String nomeEquipe = team.isPresent() ? team.get().getNome() : "Não encontrada";
                System.out.printf("   👥 Equipe: %s\n", nomeEquipe);
            }
            
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO do projeto (1, 2, 3...)");
        System.out.println("• Digite parte do NOME do projeto");

        String busca = ConsoleUtils.lerString("Selecione o projeto: ");
        return encontrarProjeto(busca, projetos);
    }

    private Team selecionarEquipe() {
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhuma equipe cadastrada!");
            System.out.println("💡 Dica: Primeiro crie uma equipe no menu 'Gerenciar Equipes'");
            return null;
        }

        System.out.println("=== EQUIPES DISPONÍVEIS ===");
        for (int i = 0; i < equipes.size(); i++) {
            Team e = equipes.get(i);
            System.out.printf("%d. %s\n", (i + 1), e.getNome());
            System.out.printf("   ID: %s | Membros: %d\n", 
                    e.getId().substring(0, 8) + "...", 
                    e.getMemberIds() != null ? e.getMemberIds().size() : 0);
            
            if (e.getDescription() != null && !e.getDescription().trim().isEmpty()) {
                System.out.printf("   Descrição: %s\n", e.getDescription());
            }
            
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO da equipe (1, 2, 3...)");
        System.out.println("• Digite o NOME da equipe");

        String busca = ConsoleUtils.lerString("Selecione a equipe: ");

        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= equipes.size()) {
                return equipes.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            for (Team equipe : equipes) {
                if (equipe.getNome().toLowerCase().contains(busca.toLowerCase())) {
                    return equipe;
                }
            }
        }

        ConsoleUtils.mostrarMensagemErro("Equipe não encontrada!");
        return null;
    }

    private Project encontrarProjeto(String busca, List<Project> projetos) {
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= projetos.size()) {
                return projetos.get(numero - 1);
            }
        } catch (NumberFormatException e) {
        }

        for (Project p : projetos) {
            if (p.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return p;
            }
        }

        for (Project p : projetos) {
            if (p.getNome().toLowerCase().contains(busca.toLowerCase())) {
                return p;
            }
        }

        return null;
    }

    private void alterarStatusProjeto(Project projeto) {
        System.out.println("\n📊 STATUS DISPONÍVEIS:");
        ProjectStatus[] statuses = ProjectStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.printf("%d. %s - %s\n", 
                    (i + 1), 
                    statuses[i].getDisplayName(),
                    statuses[i].getDescription());
        }
        
        int opcao = ConsoleUtils.lerInt("Escolha o novo status (1-" + statuses.length + "): ");
        if (opcao >= 1 && opcao <= statuses.length) {
            projeto.setStatus(statuses[opcao - 1]);
            System.out.println("✓ Status atualizado para: " + statuses[opcao - 1].getDisplayName());
        } else {
            System.out.println("❌ Opção inválida. Status não alterado.");
        }
    }

    private void excluirProjeto() {
        ConsoleUtils.mostrarTitulo("EXCLUIR PROJETO");
        
        Project projeto = selecionarProjeto("SELECIONAR PROJETO PARA EXCLUIR");
        if (projeto == null) {
            return;
        }

        System.out.println("\n🗑️ PROJETO A SER EXCLUÍDO:");
        System.out.println("Nome: " + projeto.getNome());
        System.out.println("Status: " + projeto.getStatus().getDisplayName());
        if (projeto.hasTeam()) {
            Optional<Team> team = teamRepo.findById(projeto.getTeamId());
            String nomeEquipe = team.isPresent() ? team.get().getNome() : "Não encontrada";
            System.out.println("Equipe vinculada: " + nomeEquipe);
        }

        String confirmacao = ConsoleUtils.lerString("⚠️ CONFIRMA EXCLUSÃO? (s/n): ");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            this.projectRepo.delete(projeto.getId());
            
            if (this.authService.getCurrentUser() != null) {
                this.logService.log(this.authService.getCurrentUser().getId(), "DELETE_PROJECT", 
                                   projeto.getId(), "Projeto excluído: " + projeto.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Projeto removido com sucesso!");
        } else {
            System.out.println("❌ Exclusão cancelada.");
        }
    }
}