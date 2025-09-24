package com.ProjectManager.view;

import com.ProjectManager.util.DateUtils;
import com.ProjectManager.model.Task;
import com.ProjectManager.model.TaskStatus;
import com.ProjectManager.model.Project;
import com.ProjectManager.model.User;
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

    // NOVO MÉTODO: Seleção inteligente de tarefa
    private Task selecionarTarefa() {
        List<Task> tarefas = taskRepo.findAll();
        if (tarefas.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhuma tarefa cadastrada!");
            System.out.println("💡 Dica: Primeiro crie uma tarefa");
            return null;
        }

        System.out.println("=== TAREFAS DISPONÍVEIS ===");
        for (int i = 0; i < tarefas.size(); i++) {
            Task t = tarefas.get(i);
            
            // Buscar nome do projeto
            String nomeProjeto = "Projeto não encontrado";
            Optional<Project> projeto = projectRepo.findById(t.getProjetoId());
            if (projeto.isPresent()) {
                nomeProjeto = projeto.get().getNome();
            }
            
            System.out.printf("%d. %s\n", (i + 1), t.getNome());
            System.out.printf("   ID: %s | Status: %s\n", 
                    t.getId().substring(0, 8) + "...", t.getStatus());
            System.out.printf("   Projeto: %s\n", nomeProjeto);
            System.out.printf("   Prazo: %s\n", DateUtils.formatarData(t.getPrazo()));
            
            if (t.isAtrasada()) {
                System.out.println("   ⚠️ ATRASADA!");
            }
            
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO da tarefa (1, 2, 3...)");
        System.out.println("• Digite parte do NOME da tarefa");
        System.out.println("• Digite os primeiros caracteres do ID");

        String busca = ConsoleUtils.lerString("Selecione a tarefa: ");
        
        if (busca.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Seleção não pode ser vazia!");
            return null;
        }

        return encontrarTarefa(busca.trim(), tarefas);
    }

    // NOVO MÉTODO: Busca inteligente de tarefa
    private Task encontrarTarefa(String busca, List<Task> tarefas) {
        // 1. Busca por número
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= tarefas.size()) {
                return tarefas.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Continuar
        }

        // 2. Busca por ID (primeiros caracteres)
        for (Task t : tarefas) {
            if (t.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return t;
            }
        }

        // 3. Busca por nome (contém)
        List<Task> encontradas = new java.util.ArrayList<>();
        for (Task t : tarefas) {
            if (t.getNome().toLowerCase().contains(busca.toLowerCase())) {
                encontradas.add(t);
            }
        }

        if (encontradas.isEmpty()) {
            System.out.println("❌ Tarefa não encontrada!");
            System.out.println("💡 Dicas:");
            System.out.println("   • Use o número da lista (ex: 1, 2, 3...)");
            System.out.println("   • Digite parte do nome (ex: 'sistema', 'bug')");
            System.out.println("   • Use os primeiros 8 caracteres do ID");
            return null;
        }

        if (encontradas.size() == 1) {
            return encontradas.get(0);
        }

        // Múltiplos resultados
        System.out.println("\n🔍 Encontradas " + encontradas.size() + " tarefas:");
        for (int i = 0; i < encontradas.size(); i++) {
            System.out.printf("%d. %s [%s]\n", 
                    (i + 1), 
                    encontradas.get(i).getNome(),
                    encontradas.get(i).getId().substring(0, 8));
        }
        
        int escolha = ConsoleUtils.lerInt("Qual tarefa deseja? (1-" + encontradas.size() + "): ");
        if (escolha >= 1 && escolha <= encontradas.size()) {
            return encontradas.get(escolha - 1);
        }

        return null;
    }

    // NOVO MÉTODO: Seleção inteligente de usuário
    private User selecionarUsuario() {
        List<User> usuarios = userRepo.findAll();
        if (usuarios.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhum usuário cadastrado!");
            return null;
        }

        System.out.println("=== USUÁRIOS DISPONÍVEIS ===");
        for (int i = 0; i < usuarios.size(); i++) {
            User u = usuarios.get(i);
            System.out.printf("%d. %s (%s)\n", (i + 1), u.getNomeCompleto(), u.getLogin());
            System.out.printf("   ID: %s | Perfil: %s\n", 
                    u.getId().substring(0, 8) + "...", u.getPerfil());
            System.out.printf("   Email: %s | Cargo: %s\n", u.getEmail(), u.getCargo());
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO do usuário (1, 2, 3...)");
        System.out.println("• Digite parte do NOME do usuário");
        System.out.println("• Digite o LOGIN do usuário");
        System.out.println("• Digite os primeiros caracteres do ID");

        String busca = ConsoleUtils.lerString("Selecione o usuário: ");
        
        if (busca.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Seleção não pode ser vazia!");
            return null;
        }

        return encontrarUsuario(busca.trim(), usuarios);
    }

    // NOVO MÉTODO: Busca inteligente de usuário
    private User encontrarUsuario(String busca, List<User> usuarios) {
        // 1. Busca por número
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= usuarios.size()) {
                return usuarios.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Continuar
        }

        // 2. Busca por login (exato)
        for (User u : usuarios) {
            if (u.getLogin().equalsIgnoreCase(busca)) {
                return u;
            }
        }

        // 3. Busca por ID (primeiros caracteres)
        for (User u : usuarios) {
            if (u.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return u;
            }
        }

        // 4. Busca por nome (contém)
        List<User> encontrados = new java.util.ArrayList<>();
        for (User u : usuarios) {
            if (u.getNomeCompleto().toLowerCase().contains(busca.toLowerCase())) {
                encontrados.add(u);
            }
        }

        if (encontrados.isEmpty()) {
            System.out.println("❌ Usuário não encontrado!");
            System.out.println("💡 Dicas:");
            System.out.println("   • Use o número da lista (ex: 1, 2, 3...)");
            System.out.println("   • Digite o login exato");
            System.out.println("   • Digite parte do nome");
            return null;
        }

        if (encontrados.size() == 1) {
            return encontrados.get(0);
        }

        // Múltiplos resultados
        System.out.println("\n🔍 Encontrados " + encontrados.size() + " usuários:");
        for (int i = 0; i < encontrados.size(); i++) {
            User u = encontrados.get(i);
            System.out.printf("%d. %s (%s) [%s]\n", 
                    (i + 1), 
                    u.getNomeCompleto(),
                    u.getLogin(),
                    u.getId().substring(0, 8));
        }
        
        int escolha = ConsoleUtils.lerInt("Qual usuário deseja? (1-" + encontrados.size() + "): ");
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

    // MÉTODO ATUALIZADO: Editar tarefa com seleção inteligente
    private void editarTarefa() {
        ConsoleUtils.mostrarTitulo("EDITAR TAREFA");

        Task tarefa = selecionarTarefa();
        if (tarefa == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Confirmar tarefa selecionada
        System.out.println("\n✅ TAREFA SELECIONADA:");
        System.out.println("Nome: " + tarefa.getNome());
        System.out.println("Descrição: " + tarefa.getDescricao());
        System.out.println("ID: " + tarefa.getId().substring(0, 8) + "...");
        System.out.println("Status: " + tarefa.getStatus());
        
        String confirmacao = ConsoleUtils.lerString("Confirma esta tarefa? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        String novoNome = ConsoleUtils.lerString("Novo nome (deixe vazio para manter): ");
        String novaDescricao = ConsoleUtils.lerString("Nova descrição (deixe vazio para manter): ");

        if (!novoNome.trim().isEmpty()) {
            tarefa.setNome(novoNome);
            System.out.println("✓ Nome atualizado!");
        }
        
        if (!novaDescricao.trim().isEmpty()) {
            tarefa.setDescricao(novaDescricao);
            System.out.println("✓ Descrição atualizada!");
        }

        String alterarPrazo = ConsoleUtils.lerString("Alterar prazo? (s/n): ");
        if (alterarPrazo.equalsIgnoreCase("s")) {
            LocalDate novoPrazo = DateUtils.lerData("Novo prazo");
            if (novoPrazo != null) {
                tarefa.setPrazo(novoPrazo);
                System.out.println("✓ Prazo atualizado!");
            }
        }

        try {
            tarefa.validateRequiredFields();
            taskRepo.save(tarefa);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "EDIT_TASK", 
                              tarefa.getId(), "Tarefa editada: " + tarefa.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Tarefa atualizada com sucesso!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar: " + e.getMessage());
        }
    }

    // MÉTODO ATUALIZADO: Alterar status com seleção inteligente
    private void alterarStatus() {
        ConsoleUtils.mostrarTitulo("ALTERAR STATUS DA TAREFA");

        Task tarefa = selecionarTarefa();
        if (tarefa == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        System.out.println("\n📊 STATUS ATUAL: " + tarefa.getStatus());
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

            ConsoleUtils.mostrarMensagemSucesso("✅ Status atualizado para: " + novoStatus);
        } else {
            ConsoleUtils.mostrarMensagemErro("Opção inválida!");
        }
    }

    // MÉTODO ATUALIZADO: Atribuir usuário com seleção inteligente
    private void atribuirUsuario() {
        ConsoleUtils.mostrarTitulo("ATRIBUIR USUÁRIO A UMA TAREFA");

        // Seleção inteligente de tarefa
        Task tarefa = selecionarTarefa();
        if (tarefa == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Confirmar tarefa
        System.out.println("\n✅ TAREFA SELECIONADA:");
        System.out.println("Nome: " + tarefa.getNome());
        System.out.println("Status: " + tarefa.getStatus());
        System.out.println("ID: " + tarefa.getId().substring(0, 8) + "...");
        
        String confirmacao = ConsoleUtils.lerString("Confirma esta tarefa? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Seleção inteligente de usuário
        User usuario = selecionarUsuario();
        if (usuario == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Confirmar usuário
        System.out.println("\n👤 USUÁRIO SELECIONADO:");
        System.out.println("Nome: " + usuario.getNomeCompleto());
        System.out.println("Login: " + usuario.getLogin());
        System.out.println("Cargo: " + usuario.getCargo());
        System.out.println("ID: " + usuario.getId().substring(0, 8) + "...");
        
        String confirmacaoUsuario = ConsoleUtils.lerString("Confirma este usuário? (s/n): ");
        if (!confirmacaoUsuario.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Atribuir usuário
        if (tarefa.assignToUser(usuario.getId())) {
            taskRepo.save(tarefa);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "ASSIGN_USER", 
                              tarefa.getId(), "Usuário atribuído: " + usuario.getNomeCompleto());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Usuário atribuído à tarefa com sucesso!");
            System.out.println("📋 RESUMO:");
            System.out.println("Tarefa: " + tarefa.getNome());
            System.out.println("Usuário: " + usuario.getNomeCompleto() + " (" + usuario.getLogin() + ")");
        } else {
            ConsoleUtils.mostrarMensagemErro("❌ Não é possível atribuir usuário.");
            System.out.println("💡 A tarefa deve estar com status PENDENTE para atribuição.");
        }
    }

    // MÉTODO ATUALIZADO: Excluir tarefa com seleção inteligente
    private void excluirTarefa() {
        ConsoleUtils.mostrarTitulo("EXCLUIR TAREFA");

        Task tarefa = selecionarTarefa();
        if (tarefa == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Mostrar detalhes da tarefa
        System.out.println("\n🗑️ TAREFA A SER EXCLUÍDA:");
        System.out.println("Nome: " + tarefa.getNome());
        System.out.println("Status: " + tarefa.getStatus());
        System.out.println("ID: " + tarefa.getId().substring(0, 8) + "...");

        String confirmacao = ConsoleUtils.lerString("⚠️ CONFIRMA EXCLUSÃO da tarefa '" + 
                                                   tarefa.getNome() + "'? (s/n): ");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            taskRepo.delete(tarefa.getId());
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "DELETE_TASK", 
                              tarefa.getId(), "Tarefa excluída: " + tarefa.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Tarefa removida com sucesso!");
        } else {
            ConsoleUtils.mostrarMensagemSucesso("❌ Exclusão cancelada.");
        }
    }

    // MÉTODO ATUALIZADO: Marcar como concluída com seleção inteligente
    private void marcarComoConcluida() {
        ConsoleUtils.mostrarTitulo("MARCAR TAREFA COMO CONCLUÍDA");

        Task tarefa = selecionarTarefa();
        if (tarefa == null) {
            ConsoleUtils.mostrarMensagemErro("Operação cancelada!");
            return;
        }

        // Mostrar detalhes da tarefa
        System.out.println("\n✅ TAREFA SELECIONADA:");
        System.out.println("Nome: " + tarefa.getNome());
        System.out.println("Status atual: " + tarefa.getStatus());
        System.out.println("ID: " + tarefa.getId().substring(0, 8) + "...");

        String confirmacao = ConsoleUtils.lerString("Confirma conclusão desta tarefa? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        String userId = authService.getCurrentUser().getId();
        
        if (tarefa.markAsCompleted(userId)) {
            taskRepo.save(tarefa);
            logService.log(userId, "COMPLETE_TASK", tarefa.getId(), 
                          "Tarefa concluída: " + tarefa.getNome());
            ConsoleUtils.mostrarMensagemSucesso("✅ Tarefa marcada como concluída!");
            System.out.println("📋 Status atualizado para: " + tarefa.getStatus());
        } else {
            ConsoleUtils.mostrarMensagemErro("❌ Não é possível concluir esta tarefa.");
            System.out.println("💡 A tarefa deve estar EM_ANDAMENTO para ser concluída.");
        }
    }
}