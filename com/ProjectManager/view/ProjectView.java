package com.ProjectManager.view;

import com.ProjectManager.model.Project;
import com.ProjectManager.model.ProjectStatus;
import com.ProjectManager.repository.ProjectRepository;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;
import com.ProjectManager.service.LogService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjectView {
    private ProjectRepository projectRepo;
    private UserRepository userRepo;
    private AuthenticationService authService;
    private LogService logService;

    public ProjectView() {
        this.projectRepo = ProjectRepository.getInstance();
        this.userRepo = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
        this.logService = LogService.getInstance();
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.limparTela();
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE PROJETOS");

            System.out.println("1. ➕ Criar Projeto");
            System.out.println("2. 📋 Listar Projetos");
            System.out.println("3. ✏️ Editar Projeto");
            System.out.println("4. 🚀 Alterar Status");
            System.out.println("5. 🗑️ Excluir Projeto");
            System.out.println("0. ⬅️ Voltar ao Menu Principal");

            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    criarProjeto();
                    break;
                case 2:
                    listarProjetos();
                    break;
                case 3:
                    editarProjeto();
                    break;
                case 4:
                    alterarStatus();
                    break;
                case 5:
                    excluirProjeto();
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

    private void criarProjeto() {
        ConsoleUtils.mostrarTitulo("CRIAR NOVO PROJETO");

        String nome = ConsoleUtils.lerString("Nome do projeto: ");
        String descricao = ConsoleUtils.lerString("Descrição: ");
        String prazoStr = ConsoleUtils.lerString("Data de término (yyyy-MM-dd): ");
        LocalDate prazo = LocalDate.parse(prazoStr);

        Project p = new Project(nome, descricao, prazo);
        projectRepo.save(p);

        logService.log(authService.getCurrentUser().getId(), "CREATE_PROJECT", p.getId(),
                "Projeto criado");

        ConsoleUtils.mostrarMensagemSucesso("Projeto criado com sucesso!");
    }

    private void listarProjetos() {
        ConsoleUtils.mostrarTitulo("LISTA DE PROJETOS");

        List<Project> projetos = projectRepo.findAll();
        if (projetos.isEmpty()) {
            System.out.println("Nenhum projeto cadastrado.");
            return;
        }

        for (Project p : projetos) {
            System.out.printf("- [%s] %s (Status: %s, Prazo: %s, Tarefas: %d)\n",
                    p.getId().substring(0, 6),
                    p.getNome(),
                    p.getStatus().name(),
                    p.getDataFim(),
                    p.getTaskIds().size());
        }
    }

    private void editarProjeto() {
        ConsoleUtils.mostrarTitulo("EDITAR PROJETO");

        String id = ConsoleUtils.lerString("ID do projeto: ");
        Optional<Project> opt = projectRepo.findById(id);
        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        Project p = opt.get();
        String novoNome = ConsoleUtils.lerString("Novo nome (" + p.getNome() + "): ");
        String novaDesc = ConsoleUtils.lerString("Nova descrição (" + p.getDescricao() + "): ");

        if (!novoNome.isEmpty()) p.setNome(novoNome);
        if (!novaDesc.isEmpty()) p.setDescricao(novaDesc);

        projectRepo.save(p);
        logService.log(authService.getCurrentUser().getId(), "EDIT_PROJECT", p.getId(),
                "Projeto editado");

        ConsoleUtils.mostrarMensagemSucesso("Projeto atualizado!");
    }

    private void alterarStatus() {
        ConsoleUtils.mostrarTitulo("ALTERAR STATUS DO PROJETO");

        String id = ConsoleUtils.lerString("ID do projeto: ");
        Optional<Project> opt = projectRepo.findById(id);
        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        Project p = opt.get();

        System.out.println("Status disponíveis:");
        for (ProjectStatus s : ProjectStatus.values()) {
            System.out.println("- " + s.name());
        }

        String novoStatus = ConsoleUtils.lerString("Digite o novo status: ");
        try {
            p.setStatus(ProjectStatus.valueOf(novoStatus.toUpperCase()));
            projectRepo.save(p);

            logService.log(authService.getCurrentUser().getId(), "CHANGE_PROJECT_STATUS", p.getId(),
                    "Status alterado para " + novoStatus);

            ConsoleUtils.mostrarMensagemSucesso("Status atualizado!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Status inválido!");
        }
    }

    private void excluirProjeto() {
        ConsoleUtils.mostrarTitulo("EXCLUIR PROJETO");

        String id = ConsoleUtils.lerString("ID do projeto: ");
        Optional<Project> opt = projectRepo.findById(id);
        if (opt.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Projeto não encontrado!");
            return;
        }

        Project p = opt.get();
        projectRepo.delete(p.getId());

        logService.log(authService.getCurrentUser().getId(), "DELETE_PROJECT", p.getId(),
                "Projeto excluído");

        ConsoleUtils.mostrarMensagemSucesso("Projeto removido!");
    }
}
