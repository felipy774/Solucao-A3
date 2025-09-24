package com.ProjectManager.repository;

import com.ProjectManager.model.Team;
import com.ProjectManager.model.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeamRepository {
    private static TeamRepository instance;
    private List<Team> teams;
    private final String ARQUIVO_EQUIPES = "equipes.txt";

    private TeamRepository() {
        this.teams = new ArrayList<>();
        carregarEquipes();
    }

    public static TeamRepository getInstance() {
        if (instance == null) {
            instance = new TeamRepository();
        }
        return instance;
    }

    public void save(Team team) {
        if (team == null) {
            throw new IllegalArgumentException("Team não pode ser nulo!");
        }
        
        teams.removeIf(t -> t.getId().equals(team.getId()));
        teams.add(team);
        salvarEquipes();
    }

    public void delete(String id) {
        teams.removeIf(team -> team.getId().equals(id));
        salvarEquipes();
    }

    public Optional<Team> findById(String id) {
        return teams.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    public List<Team> findAll() {
        return new ArrayList<>(teams);
    }

    public Team findByName(String name) {
        for (Team team : teams) {
            if (team.getNome().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

    // NOVO MÉTODO: Busca por nome (retorna lista para múltiplos resultados)
    public List<Team> findByNome(String nome) {
        return teams.stream()
                   .filter(t -> t.getNome().toLowerCase().contains(nome.toLowerCase()))
                   .collect(Collectors.toList());
    }

    // NOVO MÉTODO: Busca por ID prefix
    public Optional<Team> findByIdPrefix(String idPrefix) {
        return teams.stream()
                   .filter(t -> t.getId().toLowerCase().startsWith(idPrefix.toLowerCase()))
                   .findFirst();
    }

    public List<Team> findByMemberId(String userId) {
        return teams.stream()
                   .filter(t -> t.getMemberIds() != null && t.getMemberIds().contains(userId))
                   .collect(Collectors.toList());
    }

    // MÉTODO PARA SALVAR EQUIPES NO ARQUIVO
    private void salvarEquipes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_EQUIPES))) {
            for (Team team : teams) {
                // Formato: ID;NOME;DESCRICAO;MEMBER_IDS (separados por vírgula)
                String memberIds = "";
                if (team.getMemberIds() != null && !team.getMemberIds().isEmpty()) {
                    memberIds = String.join(",", team.getMemberIds());
                }
                
                String description = team.getDescription() != null ? team.getDescription() : "";
                
                String linha = String.format("%s;%s;%s;%s",
                    team.getId(),
                    team.getNome(),
                    description,
                    memberIds
                );
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao salvar equipes: " + e.getMessage());
        }
    }

    // MÉTODO PARA CARREGAR EQUIPES DO ARQUIVO
    private void carregarEquipes() {
        File arquivo = new File(ARQUIVO_EQUIPES);
        if (!arquivo.exists()) {
            return; // Arquivo será criado automaticamente quando salvar
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length >= 2) {
                    try {
                        String id = dados[0];
                        String nome = dados[1];
                        String description = dados.length > 2 ? dados[2] : "";
                        String memberIds = dados.length > 3 ? dados[3] : "";

                        Team team = new Team(nome);
                        team.setId(id);
                        
                        if (!description.trim().isEmpty()) {
                            team.setDescription(description);
                        }
                        
                        // Carregar IDs dos membros
                        if (!memberIds.trim().isEmpty()) {
                            String[] ids = memberIds.split(",");
                            for (String memberId : ids) {
                                if (!memberId.trim().isEmpty()) {
                                    team.getMemberIds().add(memberId.trim());
                                }
                            }
                        }
                        
                        teams.add(team);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar equipe: " + linha + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar equipes: " + e.getMessage());
        }
    }

    // MÉTODO PARA ESTATÍSTICAS
    public void mostrarEstatisticas() {
        System.out.println("=== ESTATÍSTICAS DAS EQUIPES ===");
        System.out.println("Total de equipes: " + teams.size());
        
        if (!teams.isEmpty()) {
            int totalMembros = teams.stream()
                                   .mapToInt(t -> t.getMemberIds() != null ? t.getMemberIds().size() : 0)
                                   .sum();
            
            int equipesComMembros = (int) teams.stream()
                                              .filter(t -> t.getMemberIds() != null && !t.getMemberIds().isEmpty())
                                              .count();
            
            System.out.println("Total de membros em equipes: " + totalMembros);
            System.out.println("Equipes com membros: " + equipesComMembros);
            System.out.println("Equipes vazias: " + (teams.size() - equipesComMembros));
            
            if (equipesComMembros > 0) {
                double mediaMembros = (double) totalMembros / equipesComMembros;
                System.out.printf("Média de membros por equipe ativa: %.1f\n", mediaMembros);
                
                Team maiorEquipe = teams.stream()
                                       .filter(t -> t.getMemberIds() != null)
                                       .max((t1, t2) -> Integer.compare(
                                           t1.getMemberIds().size(), 
                                           t2.getMemberIds().size()))
                                       .orElse(null);
                
                if (maiorEquipe != null && !maiorEquipe.getMemberIds().isEmpty()) {
                    System.out.println("Maior equipe: " + maiorEquipe.getNome() + 
                                     " (" + maiorEquipe.getMemberIds().size() + " membros)");
                }
            }
        }
    }

    // MÉTODO PARA RECARREGAR DADOS
    public void recarregar() {
        teams.clear();
        carregarEquipes();
    }

    // MÉTODO PARA BACKUP
    public void fazerBackup() {
        try {
            String nomeBackup = "backup_equipes_" + System.currentTimeMillis() + ".txt";
            java.nio.file.Files.copy(
                java.nio.file.Paths.get(ARQUIVO_EQUIPES),
                java.nio.file.Paths.get(nomeBackup)
            );
            System.out.println("Backup criado: " + nomeBackup);
        } catch (Exception e) {
            System.err.println("Erro ao criar backup: " + e.getMessage());
        }
    }
}