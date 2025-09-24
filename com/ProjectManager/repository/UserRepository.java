package com.ProjectManager.repository;

import com.ProjectManager.model.User;
import com.ProjectManager.model.UserProfile;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class UserRepository {
    private static UserRepository instance;
    private List<User> users;
    private final String ARQUIVO_USUARIOS = "usuarios.txt";

    private UserRepository() {
        this.users = new ArrayList<>();
        carregarUsuarios();
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    // Salvar usuário e persistir
    public void save(User user) {
        if (user == null || !user.isValido()) {
            throw new IllegalArgumentException("Usuário inválido!");
        }
        
        // Verifica se já existe
        Optional<User> existente = findByLogin(user.getLogin());
        if (existente.isPresent() && !existente.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Login já existe!");
        }

        // Remove se já existir (para atualização)
        users.removeIf(u -> u.getId().equals(user.getId()));
        
        // Adiciona o usuário
        users.add(user);
        
        // Salva no arquivo
        salvarUsuarios();
    }

    // Buscar todos os usuários
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    // Buscar por login
    public Optional<User> findByLogin(String login) {
        return users.stream()
                   .filter(u -> u.getLogin().equals(login))
                   .findFirst();
    }

    // Buscar por CPF
    public Optional<User> findByCpf(String cpf) {
        return users.stream()
                   .filter(u -> u.getCpf().equals(cpf))
                   .findFirst();
    }

    // Buscar por ID
    public Optional<User> findById(String id) {
        return users.stream()
                   .filter(u -> u.getId().equals(id))
                   .findFirst();
    }

    // Deletar usuário
    public void delete(User user) {
        users.removeIf(u -> u.getId().equals(user.getId()));
        salvarUsuarios();
    }

    // Salvar usuários no arquivo
    private void salvarUsuarios() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS))) {
            for (User user : users) {
                String linha = String.format("%s;%s;%s;%s;%s;%s;%s;%s;%b",
                    user.getId(),
                    user.getNomeCompleto(),
                    user.getCpf(),
                    user.getEmail(),
                    user.getCargo(),
                    user.getLogin(),
                    user.getSenha(),
                    user.getPerfil().name(),
                    user.isAtivo()
                );
                writer.write(linha);
                writer.newLine();
            }
            System.out.println("✓ Usuários salvos com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }

    // Carregar usuários do arquivo
    private void carregarUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            System.out.println("Arquivo de usuários não existe. Será criado automaticamente.");
            criarAdminPadrao();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");
                if (dados.length == 9) {
                    try {
                        User user = new User(
                            dados[0], // id
                            dados[1], // nomeCompleto
                            dados[2], // cpf
                            dados[3], // email
                            dados[4], // cargo
                            dados[5], // login
                            dados[6], // senha
                            UserProfile.valueOf(dados[7]), // perfil
                            Boolean.parseBoolean(dados[8]) // ativo
                        );
                        users.add(user);
                    } catch (Exception e) {
                        System.err.println("Erro ao carregar usuário: " + linha);
                    }
                }
            }
            System.out.println("✓ " + users.size() + " usuários carregados!");
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários: " + e.getMessage());
            criarAdminPadrao();
        }
    }

    // Criar admin padrão se não existir nenhum usuário
    private void criarAdminPadrao() {
        if (users.isEmpty()) {
            User admin = User.criarAdminPadrao();
            users.add(admin);
            salvarUsuarios();
            System.out.println("✓ Usuário administrador padrão criado!");
            System.out.println("Login: admin | Senha: admin123");
        }
    }

    // Método para backup dos dados
    public void criarBackup() {
        try {
            String nomeBackup = "backup_usuarios_" + System.currentTimeMillis() + ".txt";
            Files.copy(Paths.get(ARQUIVO_USUARIOS), Paths.get(nomeBackup));
            System.out.println("✓ Backup criado: " + nomeBackup);
        } catch (Exception e) {
            System.err.println("Erro ao criar backup: " + e.getMessage());
        }
    }

    // Limpar todos os usuários (cuidado!)
    public void limparTodos() {
        users.clear();
        salvarUsuarios();
        criarAdminPadrao();
    }

    // Estatísticas
    public void mostrarEstatisticas() {
        System.out.println("=== ESTATÍSTICAS DOS USUÁRIOS ===");
        System.out.println("Total de usuários: " + users.size());
        System.out.println("Usuários ativos: " + users.stream().filter(User::isAtivo).count());
        System.out.println("Administradores: " + users.stream().filter(u -> u.getPerfil() == UserProfile.ADMINISTRADOR).count());
        System.out.println("Gerentes: " + users.stream().filter(u -> u.getPerfil() == UserProfile.GERENTE).count());
        System.out.println("Colaboradores: " + users.stream().filter(u -> u.getPerfil() == UserProfile.COLABORADOR).count());
    }
}