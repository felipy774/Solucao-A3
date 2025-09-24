package com.ProjectManager.repository;

import com.ProjectManager.util.DateUtils;
import com.ProjectManager.model.Task;
import com.ProjectManager.model.TaskStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskRepository {
    private static TaskRepository instance;
    private List<Task> tasks;
    private final String ARQUIVO_TAREFAS = "tarefas.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private TaskRepository() {
        this.tasks = new ArrayList<>();
        carregarTarefas();
    }

    public static TaskRepository getInstance() {
        if (instance == null) {
            instance = new TaskRepository();
        }
        return instance;
    }

    // Deletar tarefa e salvar permanentemente
    public void delete(String id) {
        tasks.removeIf(task -> task.getId().equals(id));
        salvarTarefas(); // ← Salva no arquivo
    }

    // Salvar tarefa e persistir permanentemente
    public void save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Tarefa não pode ser nula!");
        }
        
        // Remove se já existir (para atualização)
        tasks.removeIf(t -> t.getId().equals(task.getId()));
        
        // Adiciona a tarefa
        tasks.add(task);
        
        // Salva no arquivo
        salvarTarefas();
    }

    // Buscar tarefa por ID
    public Optional<Task> findById(String id) {
        return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    // Buscar todas as tarefas
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    // Buscar tarefas por projeto - CORRIGIDO
    public List<Task> findByProjetoId(String projetoId) {
        return tasks.stream()
                   .filter(t -> t.getProjetoId() != null && t.getProjetoId().equals(projetoId))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Buscar tarefas por status
    public List<Task> findByStatus(TaskStatus status) {
        return tasks.stream()
                   .filter(t -> t.getStatus().equals(status))
                   .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Salvar tarefas no arquivo
    private void salvarTarefas() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TAREFAS))) {
            for (Task task : tasks) {
                String linha = String.format("%s;%s;%s;%s;%s;%s",
                    task.getId(),
                    task.getNome(),
                    task.getDescricao(),
                    task.getPrazo().format(FORMATTER),
                    task.getStatus().name(),
                    task.getProjetoId() != null ? task.getProjetoId() : ""
                );
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("✓ Tarefas salvas com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }

    // Carregar tarefas do arquivo - CORRIGIDO
    private void carregarTarefas() {
        File arquivo = new File(ARQUIVO_TAREFAS);
        if (!arquivo.exists()) {
            System.out.println("Arquivo de tarefas não existe. Será criado automaticamente.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length >= 5) { // Mínimo 5 campos
                    try {
                        LocalDate prazo = LocalDate.parse(dados[3], FORMATTER);
                        String projetoId = dados.length > 5 ? dados[5] : "";
                        
                        // CORREÇÃO: Usar construtor de 4 parâmetros e depois definir ID e status
                        Task task = new Task(dados[1], dados[2], prazo, projetoId);
                        task.setId(dados[0]);
                        task.setStatus(TaskStatus.valueOf(dados[4]));
                        
                        tasks.add(task);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar tarefa: " + linha);
                    }
                }
            }
            System.out.println("✓ " + tasks.size() + " tarefas carregadas!");
        } catch (IOException e) {
            System.err.println("Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    // Criar backup das tarefas
    public void criarBackup() {
        try {
            String nomeBackup = "backup_tarefas_" + System.currentTimeMillis() + ".txt";
            java.nio.file.Files.copy(java.nio.file.Paths.get(ARQUIVO_TAREFAS), 
                                   java.nio.file.Paths.get(nomeBackup));
            System.out.println("✓ Backup de tarefas criado: " + nomeBackup);
        } catch (Exception e) {
            System.err.println("Erro ao criar backup: " + e.getMessage());
        }
    }

    // Estatísticas das tarefas
    public void mostrarEstatisticas() {
        System.out.println("=== ESTATÍSTICAS DAS TAREFAS ===");
        System.out.println("Total de tarefas: " + tasks.size());
        System.out.println("Pendentes: " + tasks.stream().filter(t -> t.getStatus() == TaskStatus.PENDENTE).count());
        System.out.println("Em andamento: " + tasks.stream().filter(t -> t.getStatus() == TaskStatus.EM_ANDAMENTO).count());
        System.out.println("Concluídas: " + tasks.stream().filter(t -> t.getStatus() == TaskStatus.CONCLUIDO).count());
        
        long atrasadas = tasks.stream()
                             .filter(t -> t.getPrazo().isBefore(LocalDate.now()) && 
                                         t.getStatus() != TaskStatus.CONCLUIDO)
                             .count();
        System.out.println("Atrasadas: " + atrasadas);
    }

    // Limpar todas as tarefas
    public void limparTodas() {
        tasks.clear();
        salvarTarefas();
    }
}
