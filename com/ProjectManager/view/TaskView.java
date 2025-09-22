package com.ProjectManager.view;

import com.ProjectManager.model.Task;
import com.ProjectManager.model.TaskStatus;
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

    private void criarTarefa() {
        ConsoleUtils.mostrarTitulo("CRIAR NOVA TAREFA");

        String nome = ConsoleUtils.lerString("Título da tarefa: ");
        String descricao = ConsoleUtils.lerString("Descrição: ");
        String prazoStr = ConsoleUtils.lerString("Prazo (yyyy-MM-dd): ");

        LocalDate prazo = LocalDate.parse(prazoStr);

        String projetoId = ConsoleUtils.lerString("ID do projeto associado: ");
        if (projectRepo.findById(projetoId).isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        Task nova = new Task(nome, descricao, prazo, projetoId);
        taskRepo.save(nova);

        logService.log(authService.getCurrentUser().getId(), "CREATE_TASK", nova.getId(), "Tarefa criada");

        ConsoleUtils.mostrarMensagemSucesso("Tarefa criada com sucesso!");
    }

    private void listarTarefas() {
        ConsoleUtils.mostrarTitulo("LISTA DE TAREFAS");

        List<Task> tarefas = taskRepo.findAll();
        if (tarefas.isEmpty()) {
            System.out.println("Nenhuma tarefa cadastrada.");
            return;
        }

        for (Task t : tarefas) {
            System.out.printf("- [%s] %s (Projeto: %s, Status: %s, Prazo: %s)\n",
                    t.getId().substring(0, 6),
                    t.getTitulo(),
                    t.getProjetoId(),
                    t.getStatus().name(),
                    t.getPrazo());
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
        String novoTitulo = ConsoleUtils.lerString("Novo título (" + tarefa.getTitulo() + "): ");
        String novaDescricao = ConsoleUtils.lerString("Nova descrição (" + tarefa.getDescricao() + "): ");

        if (!novoTitulo.isEmpty()) tarefa.setTitulo(novoTitulo);
        if (!novaDescricao.isEmpty()) tarefa.setDescricao(novaDescricao);

        taskRepo.save(tarefa);
        logService.log(authService.getCurrentUser().getId(), "EDIT_TASK", tarefa.getId(), "Tarefa editada");

        ConsoleUtils.mostrarMensagemSucesso("Tarefa atualizada!");
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

        System.out.println("Status disponíveis:");
        for (TaskStatus s : TaskStatus.values()) {
            System.out.println("- " + s.name());
        }

        String novoStatus = ConsoleUtils.lerString("Digite o novo status: ");
        try {
            tarefa.setStatus(TaskStatus.valueOf(novoStatus.toUpperCase()));
            taskRepo.save(tarefa);

            logService.log(authService.getCurrentUser().getId(), "CHANGE_STATUS", tarefa.getId(),
                    "Status alterado para " + novoStatus);

            ConsoleUtils.mostrarMensagemSucesso("Status atualizado!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Status inválido!");
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

        tarefa.setUsuarioId(idUsuario);
        taskRepo.save(tarefa);

        logService.log(authService.getCurrentUser().getId(), "ASSIGN_USER", tarefa.getId(),
                "Usuário atribuído: " + idUsuario);

        ConsoleUtils.mostrarMensagemSucesso("Usuário atribuído à tarefa!");
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
        taskRepo.delete(tarefa.getId());

        logService.log(authService.getCurrentUser().getId(), "DELETE_TASK", tarefa.getId(), "Tarefa excluída");

        ConsoleUtils.mostrarMensagemSucesso("Tarefa removida!");
    }
}
