package com.ProjectManager.repository;

import com.ProjectManager.util.DateUtils;
import com.ProjectManager.model.Project;
import com.ProjectManager.model.ProjectStatus;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectRepository {
    private static ProjectRepository instance;
    private List<Project> projects;
    private final String ARQUIVO_PROJETOS = "projetos.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ProjectRepository() {
        this.projects = new ArrayList<>();
        carregarProjetos();
    }

    public static ProjectRepository getInstance() {
        if (instance == null) {
            instance = new ProjectRepository();
        }
        return instance;
    }

    // Deletar projeto e salvar permanentemente
    public void delete(String id) {
        projects.removeIf(project -> project.getId().equals(id));
        salvarProjetos();
    }

    // Salvar projeto e persistir permanentemente
    public void save(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Projeto não pode ser nulo!");
        }
        
        projects.removeIf(p -> p.getId().equals(project.getId()));
        projects.add(project);
        salvarProjetos();
    }

    public Optional<Project> findById(String id) {
        return projects.stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public List<Project> findAll() {
        return new ArrayList<>(projects);
    }

    public List<Project> findByStatus(ProjectStatus status) {
        return projects.stream()
                      .filter(p -> p.getStatus().equals(status))
                      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // NOVOS MÉTODOS DE BUSCA - Adicionados para facilitar a busca
    public Optional<Project> findByIdPrefix(String idPrefix) {
        return projects.stream()
                      .filter(p -> p.getId().toLowerCase().startsWith(idPrefix.toLowerCase()))
                      .findFirst();
    }

    public List<Project> findByNome(String nome) {
        return projects.stream()
                      .filter(p -> p.getNome().toLowerCase().contains(nome.toLowerCase()))
                      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Salvar projetos no arquivo - CORRIGIDO para lidar com prazo nulo
    private void salvarProjetos() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_PROJETOS))) {
            for (Project project : projects) {
                String prazoStr = "";
                if (project.getPrazo() != null) {
                    prazoStr = project.getPrazo().format(FORMATTER);
                }
                
                String linha = String.format("%s;%s;%s;%s;%s;%s",
                    project.getId(),
                    project.getNome(),
                    project.getDescricao(),
                    prazoStr, // ← CORREÇÃO: Tratamento de prazo nulo
                    project.getStatus().name(),
                    project.getGerenteId() != null ? project.getGerenteId() : ""
                );
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("✓ Projetos salvos com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar projetos: " + e.getMessage());
        }
    }

    // Carregar projetos do arquivo - CORRIGIDO para lidar com prazo vazio
    private void carregarProjetos() {
        File arquivo = new File(ARQUIVO_PROJETOS);
        if (!arquivo.exists()) {
            System.out.println("Arquivo de projetos não existe. Será criado automaticamente.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length >= 5) {
                    try {
                        // CORREÇÃO: Tratar prazo vazio
                        LocalDate prazo = null;
                        if (dados[3] != null && !dados[3].trim().isEmpty()) {
                            prazo = LocalDate.parse(dados[3], FORMATTER);
                        }
                        
                        String gerenteId = dados.length > 5 ? dados[5] : "";
                        
                        Project project = new Project(dados[1], dados[2], prazo, gerenteId);
                        project.setId(dados[0]);
                        
                        // CORREÇÃO: Verificar se o status existe no enum
                        try {
                            project.setStatus(ProjectStatus.valueOf(dados[4]));
                        } catch (IllegalArgumentException e) {
                            // Se o status não existe, usar um padrão
                            System.err.println("Status inválido '" + dados[4] + "' para projeto " + dados[1] + ". Usando PLANEJADO.");
                            project.setStatus(ProjectStatus.PLANEJADO);
                        }
                        
                        projects.add(project);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar projeto: " + linha + " - " + e.getMessage());
                    }
                }
            }
            System.out.println("✓ " + projects.size() + " projetos carregados!");
        } catch (IOException e) {
            System.err.println("Erro ao carregar projetos: " + e.getMessage());
        }
    }

    // CORREÇÃO: Estatísticas com verificação de enum válido
    public void mostrarEstatisticas() {
        System.out.println("=== ESTATÍSTICAS DOS PROJETOS ===");
        System.out.println("Total de projetos: " + projects.size());
        
        // Verificar quais status existem no enum antes de contar
        long planejamento = projects.stream().filter(p -> {
            try {
                return p.getStatus() == ProjectStatus.PLANEJAMENTO;
            } catch (Exception e) {
                return false;
            }
        }).count();
        
        long emAndamento = projects.stream().filter(p -> {
            try {
                return p.getStatus() == ProjectStatus.EM_ANDAMENTO;
            } catch (Exception e) {
                return false;
            }
        }).count();
        
        long concluidos = projects.stream().filter(p -> {
            try {
                return p.getStatus() == ProjectStatus.CONCLUIDO;
            } catch (Exception e) {
                return false;
            }
        }).count();
        
        System.out.println("Em planejamento: " + planejamento);
        System.out.println("Em andamento: " + emAndamento);
        System.out.println("Concluídos: " + concluidos);
        
        // NOVO: Estatísticas adicionais
        long comPrazo = projects.stream().filter(p -> p.getPrazo() != null).count();
        long semPrazo = projects.size() - comPrazo;
        
        System.out.println("Com prazo definido: " + comPrazo);
        System.out.println("Sem prazo definido: " + semPrazo);
        
        if (comPrazo > 0) {
            long atrasados = projects.stream()
                                   .filter(p -> p.getPrazo() != null && 
                                              p.getPrazo().isBefore(LocalDate.now()) &&
                                              p.getStatus() != ProjectStatus.CONCLUIDO)
                                   .count();
            System.out.println("Projetos atrasados: " + atrasados);
        }
    }

    // NOVOS MÉTODOS ÚTEIS - Mantendo todas as funcionalidades
    public void criarBackup() {
        try {
            String nomeBackup = "backup_projetos_" + System.currentTimeMillis() + ".txt";
            java.nio.file.Files.copy(java.nio.file.Paths.get(ARQUIVO_PROJETOS), 
                                   java.nio.file.Paths.get(nomeBackup));
            System.out.println("✓ Backup de projetos criado: " + nomeBackup);
        } catch (Exception e) {
            System.err.println("Erro ao criar backup: " + e.getMessage());
        }
    }

    public void limparTodos() {
        projects.clear();
        salvarProjetos();
        System.out.println("✓ Todos os projetos foram removidos!");
    }

    public List<Project> findByGerente(String gerenteId) {
        return projects.stream()
                      .filter(p -> p.getGerenteId() != null && p.getGerenteId().equals(gerenteId))
                      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public List<Project> findAtrasados() {
        return projects.stream()
                      .filter(p -> p.getPrazo() != null && 
                                 p.getPrazo().isBefore(LocalDate.now()) &&
                                 p.getStatus() != ProjectStatus.CONCLUIDO)
                      .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Método para debug - ÚTIL para encontrar problemas
    public void debugInfo() {
        System.out.println("=== DEBUG INFO - PROJETOS ===");
        System.out.println("Total em memória: " + projects.size());
        System.out.println("Arquivo existe: " + new File(ARQUIVO_PROJETOS).exists());
        
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            System.out.printf("%d. %s [%s...] - Status: %s\n", 
                    (i+1), p.getNome(), p.getId().substring(0, 8), p.getStatus());
        }
    }
}