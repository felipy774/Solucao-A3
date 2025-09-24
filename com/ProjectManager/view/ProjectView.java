package com.ProjectManager.view;

import com.ProjectManager.model.Project;
import com.ProjectManager.model.ProjectStatus;
import com.ProjectManager.repository.ProjectRepository;
import com.ProjectManager.service.AuthenticationService;
import com.ProjectManager.service.LogService;
import com.ProjectManager.util.DateUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjectView {
    private ProjectRepository projectRepo = ProjectRepository.getInstance();
    private AuthenticationService authService = AuthenticationService.getInstance();
    private LogService logService = LogService.getInstance();

    public ProjectView() {
        // Construtor sem Scanner - usando ConsoleUtils
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.limparTela();
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE PROJETOS");
            System.out.println("1. ➕ Criar Projeto");
            System.out.println("2. 📋 Listar Projetos");
            System.out.println("3. ✏️ Editar Projeto");
            System.out.println("4. 🗑️ Excluir Projeto");
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
            
            // CORREÇÃO: Verificar se usuário logado existe
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
            System.out.printf("   Status: %s\n", projeto.getStatus());
            
            if (projeto.getPrazo() != null) {
                System.out.printf("   Prazo: %s\n", DateUtils.formatarData(projeto.getPrazo()));
            }

            if (projeto.getGerenteId() != null && !projeto.getGerenteId().trim().isEmpty()) {
                System.out.printf("   Gerente: %s\n", projeto.getGerenteId());
            }
            
            System.out.println("   " + "-".repeat(40));
        }
    }

    private void editarProjeto() {
        ConsoleUtils.mostrarTitulo("EDITAR PROJETO");
        
        List<Project> projetos = projectRepo.findAll();
        if (projetos.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Nenhum projeto cadastrado!");
            return;
        }

        System.out.println("=== PROJETOS DISPONÍVEIS ===");
        for (int i = 0; i < projetos.size(); i++) {
            Project p = projetos.get(i);
            System.out.printf("%d. %s\n", (i + 1), p.getNome());
            System.out.printf("   ID: %s | Status: %s\n", 
                    p.getId().substring(0, 8) + "...", p.getStatus());
            if (p.getPrazo() != null) {
                System.out.printf("   Prazo: %s\n", DateUtils.formatarData(p.getPrazo()));
            }
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE BUSCAR:");
        System.out.println("• Digite o NÚMERO do projeto (1, 2, 3...)");
        System.out.println("• Digite parte do NOME do projeto");
        System.out.println("• Digite os primeiros caracteres do ID");

        String busca = ConsoleUtils.lerString("Como deseja buscar o projeto? ");
        
        if (busca.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Busca não pode ser vazia!");
            return;
        }

        Project projeto = encontrarProjeto(busca.trim(), projetos);
        
        if (projeto == null) {
            ConsoleUtils.mostrarMensagemErro("❌ Projeto não encontrado!");
            System.out.println("💡 Dicas:");
            System.out.println("   • Use o número da lista (ex: 1, 2, 3...)");
            System.out.println("   • Digite parte do nome (ex: 'sistema', 'web')");
            System.out.println("   • Use os primeiros 8 caracteres do ID");
            return;
        }

        // Confirmar projeto
        System.out.println("\n✅ PROJETO ENCONTRADO:");
        System.out.println("Nome: " + projeto.getNome());
        System.out.println("Descrição: " + projeto.getDescricao());
        System.out.println("ID: " + projeto.getId().substring(0, 8) + "...");
        System.out.println("Status: " + projeto.getStatus());
        if (projeto.getPrazo() != null) {
            System.out.println("Prazo: " + DateUtils.formatarData(projeto.getPrazo()));
        }
        
        String confirmacao = ConsoleUtils.lerString("\nÉ este projeto que deseja editar? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Edição
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

        String alterarStatus = ConsoleUtils.lerString("Deseja alterar o status? (s/n): ");
        if (alterarStatus.equalsIgnoreCase("s")) {
            alterarStatusProjeto(projeto);
        }

        try {
            projectRepo.save(projeto);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "EDIT_PROJECT", 
                              projeto.getId(), "Projeto editado: " + projeto.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Projeto atualizado com sucesso!");
            
            System.out.println("\n📋 PROJETO ATUALIZADO:");
            System.out.println("Nome: " + projeto.getNome());
            System.out.println("Descrição: " + projeto.getDescricao());
            System.out.println("Status: " + projeto.getStatus());
            if (projeto.getPrazo() != null) {
                System.out.println("Prazo: " + DateUtils.formatarData(projeto.getPrazo()));
            }
            
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar: " + e.getMessage());
        }
    }

    // CORREÇÃO: Método encontrarProjeto sem usar métodos que podem não existir
    private Project encontrarProjeto(String busca, List<Project> projetos) {
        // 1. Busca por número
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= projetos.size()) {
                return projetos.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Continuar
        }

        // 2. Busca por ID (primeiros caracteres)
        for (Project p : projetos) {
            if (p.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return p;
            }
        }

        // 3. Busca por nome (contém)
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
            System.out.printf("%d. %s\n", (i + 1), statuses[i].name());
        }
        
        int opcao = ConsoleUtils.lerInt("Escolha o novo status (1-" + statuses.length + "): ");
        if (opcao >= 1 && opcao <= statuses.length) {
            projeto.setStatus(statuses[opcao - 1]);
            System.out.println("✓ Status atualizado para: " + statuses[opcao - 1].name());
        } else {
            System.out.println("❌ Opção inválida. Status não alterado.");
        }
    }

    private void excluirProjeto() {
        ConsoleUtils.mostrarTitulo("EXCLUIR PROJETO");
        
        List<Project> projetos = this.projectRepo.findAll();
        if (projetos.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Nenhum projeto cadastrado!");
            return;
        }

        // Mostrar projetos numerados
        System.out.println("=== PROJETOS DISPONÍVEIS ===");
        for (int i = 0; i < projetos.size(); i++) {
            Project p = projetos.get(i);
            System.out.printf("%d. %s [%s]\n", 
                    (i + 1), p.getNome(), p.getId().substring(0, 8));
        }

        String busca = ConsoleUtils.lerString("Digite o número ou nome do projeto: ");
        Project projeto = encontrarProjeto(busca, projetos);
        
        if (projeto == null) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        String confirmacao = ConsoleUtils.lerString("Confirma exclusão do projeto '" + 
                                                   projeto.getNome() + "'? (s/n): ");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            this.projectRepo.delete(projeto.getId());
            
            if (this.authService.getCurrentUser() != null) {
                this.logService.log(this.authService.getCurrentUser().getId(), "DELETE_PROJECT", 
                                   projeto.getId(), "Projeto excluído");
            }
            
            ConsoleUtils.mostrarMensagemSucesso("Projeto removido com sucesso!");
        } else {
            ConsoleUtils.mostrarMensagemSucesso("Exclusão cancelada.");
        }
    }
}