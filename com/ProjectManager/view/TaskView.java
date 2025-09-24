package com.ProjectManager.view;

import com.ProjectManager.util.DateUtils;
import com.ProjectManager.model.Task;
import com.ProjectManager.model.TaskStatus;
import com.ProjectManager.model.Project;
import com.ProjectManager.repository.TaskRepository;
import com.ProjectManager.repository.ProjectRepository;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;
import com.ProjectManager.service.LogService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TaskView {
    private TaskRepository taskRepo;
    private ProjectRepository projectRepo;
    private UserRepository userRepo;
    private AuthenticationService authService;
    private LogService logService;

    public TaskView() {
        this.taskRepo = TaskRepository.getInstance();
        this.projectRepo = ProjectRepository.getInstance();
        this.userRepo = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
        this.logService = LogService.getInstance();
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.limparTela();
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE TAREFAS");

            System.out.println("1. ➕ Criar Tarefa");
            System.out.println("2. 📋 Listar Tarefas");
            System.out.println("3. ✏️ Editar Tarefa");
            System.out.println("4. 🚀 Alterar Status");
            System.out.println("5. 👥 Atribuir Usuário");
            System.out.println("6. 🗑️ Excluir Tarefa");
            System.out.println("7. ✅ Marcar como Concluída");
            System.out.println("0. ⬅️ Voltar ao Menu Principal");

            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    criarTarefa();
                    break;
                case 2:
                    listarTarefas();
                    break;
                case 3:
                    editarTarefa();
                    break;
                case 4:
                    alterarStatus();
                    break;
                case 5:
                    atribuirUsuario();
                    break;
                case 6:
                    excluirTarefa();
                    break;
                case 7:
                    marcarComoConcluida();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    ConsoleUtils.mostrarMensagemErro("Opção inválida!");
            }

            if (!voltar) {
                ConsoleUtils.pausar();
            }
        }
    }

    // NOVO MÉTODO: Seleção inteligente de projeto
    private Project selecionarProjeto() {
        List<Project> projetos = projectRepo.findAll();
        if (projetos.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhum projeto cadastrado!");
            System.out.println("💡 Dica: Primeiro crie um projeto no menu 'Gerenciar Projetos'");
            return null;
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

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO do projeto (1, 2, 3...)");
        System.out.println("• Digite parte do NOME do projeto");
        System.out.println("• Digite os primeiros caracteres do ID");

        String busca = ConsoleUtils.lerString("Selecione o projeto: ");
        
        if (busca.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Seleção não pode ser vazia!");
            return null;
        }

        return encontrarProjeto(busca.trim(), projetos);
    }

    // NOVO MÉTODO: Busca inteligente de projeto
    private Project encontrarProjeto(String busca, List<Project> projetos) {
        // 1. Busca por número
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= projetos.size()) {
                return projetos.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Continuar com outras buscas
        }

        // 2. Busca por ID (primeiros caracteres)
        for (Project p : projetos) {
            if (p.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return p;
            }
        }

        // 3. Busca por nome (contém)
        List<Project> encontrados = new java.util.ArrayList<>();
        for (Project p : projetos) {
            if (p.getNome().toLowerCase().contains(busca.toLowerCase())) {
                encontrados.add(p);
            }
        }

        if (encontrados.isEmpty()) {
            System.out.println("❌ Projeto não encontrado!");
            System.out.println("💡 Dicas:");
            System.out.println("   • Use o número da lista (ex: 1, 2, 3...)");
            System.out.println("   • Digite parte do nome (ex: 'sistema', 'web')");
            System.out.println("   • Use os primeiros 8 caracteres do ID");
            return null;
        }

        if (encontrados.size() == 1) {
            return encontrados.get(0);
        }

        // Múltiplos resultados - deixar escolher
        System.out.println("\n🔍 Encontrados " + encontrados.size() + " projetos:");
        for (int i = 0; i < encontrados.size(); i++) {
            System.out.printf("%d. %s [%s]\n", 
                    (i + 1), 
                    encontrados.get(i).getNome(),
                    encontrados.get(i).getId().substring(0, 8));
        }
        
        int escolha = ConsoleUtils.lerInt("Qual projeto deseja? (1-" + encontrados.size() + "): ");
        if (escolha >= 1 && escolha <= encontrados.size()) {
            return encontrados.get(escolha - 1);
        }

        return null;
    }

    // MÉTODO ATUALIZADO: Criar tarefa com seleção inteligente
    private void criarTarefa() {
        ConsoleUtils.mostrarTitulo("CRIAR NOVA TAREFA");

        // USAR SELEÇÃO INTELIGENTE DE PROJETO
        Project projeto = selecionarProjeto();
        if (projeto == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Confirmar projeto selecionado
        System.out.println("\n✅ PROJETO SELECIONADO:");
        System.out.println("Nome: " + projeto.getNome());
        System.out.println("Descrição: " + projeto.getDescricao());
        System.out.println("ID: " + projeto.getId().substring(0, 8) + "...");
        System.out.println("Status: " + projeto.getStatus());
        
        String confirmacao = ConsoleUtils.lerString("Confirma este projeto? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Dados da tarefa
        String nome = ConsoleUtils.lerString("Nome da tarefa: ");
        if (nome.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Nome da tarefa é obrigatório!");
            return;
        }

        String descricao = ConsoleUtils.lerString("Descrição: ");
        if (descricao.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Descrição é obrigatória!");
            return;
        }

        // USAR DateUtils para entrada segura de data
        LocalDate prazo = DateUtils.lerData("Prazo da tarefa");
        if (prazo == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        try {
            // Usar o ID do projeto selecionado
            Task nova = new Task(nome, descricao, prazo, projeto.getId());
            nova.validateRequiredFields();
            taskRepo.save(nova);

            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "CREATE_TASK", 
                              nova.getId(), "Tarefa criada no projeto: " + projeto.getNome());
            }

            ConsoleUtils.mostrarMensagemSucesso("✅ Tarefa criada com sucesso!");
            System.out.println("📋 RESUMO DA TAREFA:");
            System.out.println("Nome: " + nova.getNome());
            System.out.println("Projeto: " + projeto.getNome());
            System.out.println("Prazo: " + DateUtils.formatarData(prazo));
            System.out.println("Status: " + nova.getStatus());
            
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao criar tarefa: " + e.getMessage());
        }
    }

    private void listarTarefas() {
        ConsoleUtils.mostrarTitulo("LISTA DE TAREFAS");

        List<Task> tarefas = taskRepo.findAll();
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
            return;
        }

        System.out.println("=== TAREFAS CADASTRADAS ===");
        for (int i = 0; i < tarefas.size(); i++) {
            Task t = tarefas.get(i);
            
            // Buscar nome do projeto
            String nomeProjeto = "Projeto não encontrado";
            Optional<Project> projeto = projectRepo.findById(t.getProjetoId());
            if (projeto.isPresent()) {
                nomeProjeto = projeto.get().getNome();
            }
            
            System.out.printf("%d. [%s] %s\n", (i + 1), t.getId().substring(0, 8), t.getNome());
            System.out.printf("   Projeto: %s\n", nomeProjeto);
            System.out.printf("   Status: %s\n", t.getStatus().name());
            System.out.printf("   Prazo: %s\n", DateUtils.formatarData(t.getPrazo()));
            
            if (t.isAtrasada()) {
                System.out.println("   ⚠️ TAREFA ATRASADA!");
            }
            
            System.out.println("   " + "-".repeat(40));
        }
    }

    private void editarTarefa() {
        ConsoleUtils.mostrarTitulo("EDITAR TAREFA");

        String id = ConsoleUtils.lerString("ID da tarefa: ");
        Optional<Task> opt = taskRepo.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Tarefa não encontrada!");
            return;
        }

        Task tarefa = opt.get();
        String novoNome = ConsoleUtils.lerString("Novo nome (" + tarefa.getNome() + "): ");
        String novaDescricao = ConsoleUtils.lerString("Nova descrição (" + tarefa.getDescricao() + "): ");

        if (!novoNome.isEmpty()) tarefa.setNome(novoNome);
        if (!novaDescricao.isEmpty()) tarefa.setDescricao(novaDescricao);

        String alterarPrazo = ConsoleUtils.lerString("Alterar prazo? (s/n): ");
        if (alterarPrazo.equalsIgnoreCase("s")) {
            LocalDate novoPrazo = DateUtils.lerData("Novo prazo");
            if (novoPrazo != null) {
                tarefa.setPrazo(novoPrazo);
            }
        }

        try {
            tarefa.validateRequiredFields();
            taskRepo.save(tarefa);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "EDIT_TASK", 
                              tarefa.getId(), "Tarefa editada");
            }
            
            ConsoleUtils.mostrarMensagemSucesso("Tarefa atualizada!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar: " + e.getMessage());
        }
    }

    private void alterarStatus() {
        ConsoleUtils.mostrarTitulo("ALTERAR STATUS DA TAREFA");

        String id = ConsoleUtils.lerString("ID da tarefa: ");
        Optional<Task> opt = taskRepo.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Tarefa não encontrada!");
            return;
        }

        Task tarefa = opt.get();

        System.out.println("Status atual: " + tarefa.getStatus());
        System.out.println("\nStatus disponíveis:");
        TaskStatus[] statuses = TaskStatus.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i].name());
        }

        int opcaoStatus = ConsoleUtils.lerInt("Escolha o novo status (1-" + statuses.length + "): ");
        
        if (opcaoStatus >= 1 && opcaoStatus <= statuses.length) {
            TaskStatus novoStatus = statuses[opcaoStatus - 1];
            tarefa.setStatus(novoStatus);
            taskRepo.save(tarefa);

            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "CHANGE_STATUS", 
                              tarefa.getId(), "Status alterado para " + novoStatus);
            }

            ConsoleUtils.mostrarMensagemSucesso("Status atualizado para: " + novoStatus);
        } else {
            ConsoleUtils.mostrarMensagemErro("Opção inválida!");
        }
    }

    private void atribuirUsuario() {
        ConsoleUtils.mostrarTitulo("ATRIBUIR USUÁRIO A UMA TAREFA");

        String idTarefa = ConsoleUtils.lerString("ID da tarefa: ");
        Optional<Task> opt = taskRepo.findById(idTarefa);

        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Tarefa não encontrada!");
            return;
        }

        Task tarefa = opt.get();

        String idUsuario = ConsoleUtils.lerString("ID do usuário: ");
        if (userRepo.findById(idUsuario).isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Usuário não encontrado!");
            return;
        }

        if (tarefa.assignToUser(idUsuario)) {
            taskRepo.save(tarefa);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "ASSIGN_USER", 
                              tarefa.getId(), "Usuário atribuído: " + idUsuario);
            }
            
            ConsoleUtils.mostrarMensagemSucesso("Usuário atribuído à tarefa!");
        } else {
            ConsoleUtils.mostrarMensagemErro("Não é possível atribuir usuário. Tarefa deve estar PENDENTE.");
        }
    }

    private void excluirTarefa() {
        ConsoleUtils.mostrarTitulo("EXCLUIR TAREFA");

        String id = ConsoleUtils.lerString("ID da tarefa: ");
        Optional<Task> opt = taskRepo.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Tarefa não encontrada!");
            return;
        }

        Task tarefa = opt.get();
        String confirmacao = ConsoleUtils.lerString("Confirma exclusão da tarefa '" + tarefa.getNome() + "'? (s/n): ");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            taskRepo.delete(tarefa.getId());
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "DELETE_TASK", 
                              tarefa.getId(), "Tarefa excluída");
            }
            
            ConsoleUtils.mostrarMensagemSucesso("Tarefa removida com sucesso!");
        } else {
            ConsoleUtils.mostrarMensagemSucesso("Exclusão cancelada.");
        }
    }

    private void marcarComoConcluida() {
        ConsoleUtils.mostrarTitulo("MARCAR TAREFA COMO CONCLUÍDA");

        String id = ConsoleUtils.lerString("ID da tarefa: ");
        Optional<Task> opt = taskRepo.findById(id);

        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Tarefa não encontrada!");
            return;
        }

        Task tarefa = opt.get();
        String userId = authService.getCurrentUser().getId();
        
        if (tarefa.markAsCompleted(userId)) {
            taskRepo.save(tarefa);
            logService.log(userId, "COMPLETE_TASK", tarefa.getId(), "Tarefa concluída");
            ConsoleUtils.mostrarMensagemSucesso("Tarefa marcada como concluída!");
        } else {
            ConsoleUtils.mostrarMensagemErro("Não é possível concluir. Tarefa deve estar EM_ANDAMENTO.");
        }
    }
}