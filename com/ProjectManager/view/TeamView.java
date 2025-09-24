package com.ProjectManager.view;

import java.util.List;
import java.util.Optional;

import com.ProjectManager.model.Team;
import com.ProjectManager.model.User;
import com.ProjectManager.repository.TeamRepository;
import com.ProjectManager.repository.UserRepository;
import com.ProjectManager.service.AuthenticationService;
import com.ProjectManager.service.LogService;

public class TeamView {
    private TeamRepository teamRepo;
    private UserRepository userRepo;
    private AuthenticationService authService;
    private LogService logService;

    public TeamView() {
        this.teamRepo = TeamRepository.getInstance();
        this.userRepo = UserRepository.getInstance();
        this.authService = AuthenticationService.getInstance();
        this.logService = LogService.getInstance();
    }

    public void mostrarMenu() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleUtils.limparTela();
            ConsoleUtils.mostrarTitulo("GERENCIAMENTO DE EQUIPES");
            System.out.println("1. ➕ Criar equipe");
            System.out.println("2. 📋 Listar equipes");
            System.out.println("3. ✏️ Editar equipe");
            System.out.println("4. 👥 Adicionar membro");
            System.out.println("5. ➖ Remover membro");
            System.out.println("6. 🗑️ Excluir equipe");
            System.out.println("7. 📊 Estatísticas");
            System.out.println("0. ⬅️ Voltar");
            
            int opcao = ConsoleUtils.lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1:
                    criarEquipe();
                    break;
                case 2:
                    listarEquipes();
                    break;
                case 3:
                    editarEquipe();
                    break;
                case 4:
                    adicionarMembro();
                    break;
                case 5:
                    removerMembro();
                    break;
                case 6:
                    excluirEquipe();
                    break;
                case 7:
                    mostrarEstatisticas();
                    break;
                case 0:
                    voltar = true;
                    break;
                default:
                    ConsoleUtils.mostrarMensagemErro("Opção inválida! Tente novamente.");
            }

            if (!voltar) {
                ConsoleUtils.pausar();
            }
        }
    }

    // NOVO MÉTODO: Seleção inteligente de equipe
    private Team selecionarEquipe() {
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhuma equipe cadastrada!");
            System.out.println("💡 Dica: Primeiro crie uma equipe");
            return null;
        }

        System.out.println("=== EQUIPES DISPONÍVEIS ===");
        for (int i = 0; i < equipes.size(); i++) {
            Team t = equipes.get(i);
            System.out.printf("%d. %s\n", (i + 1), t.getNome());
            System.out.printf("   ID: %s | Membros: %d\n", 
                    t.getId().substring(0, 8) + "...", 
                    t.getMembros() != null ? t.getMembros().size() : 0);
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO da equipe (1, 2, 3...)");
        System.out.println("• Digite o NOME COMPLETO da equipe");
        System.out.println("• Digite os primeiros caracteres do ID");

        String busca = ConsoleUtils.lerString("Selecione a equipe: ");
        
        if (busca.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Seleção não pode ser vazia!");
            return null;
        }

        return encontrarEquipe(busca.trim(), equipes);
    }

    // NOVO MÉTODO: Busca inteligente de equipe
    private Team encontrarEquipe(String busca, List<Team> equipes) {
        // 1. Busca por número
        try {
            int numero = Integer.parseInt(busca);
            if (numero >= 1 && numero <= equipes.size()) {
                return equipes.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Continuar com outras buscas
        }

        // 2. Busca por nome EXATO (método original)
        for (Team t : equipes) {
            if (t.getNome().equalsIgnoreCase(busca)) {
                return t;
            }
        }

        // 3. Busca por ID (primeiros caracteres)
        for (Team t : equipes) {
            if (t.getId().toLowerCase().startsWith(busca.toLowerCase())) {
                return t;
            }
        }

        // 4. Busca por nome parcial (nova funcionalidade)
        List<Team> encontradas = new java.util.ArrayList<>();
        for (Team t : equipes) {
            if (t.getNome().toLowerCase().contains(busca.toLowerCase())) {
                encontradas.add(t);
            }
        }

        if (encontradas.isEmpty()) {
            System.out.println("❌ Equipe não encontrada!");
            System.out.println("💡 Dicas:");
            System.out.println("   • Use o número da lista (ex: 1, 2, 3...)");
            System.out.println("   • Digite o nome completo da equipe");
            System.out.println("   • Use os primeiros 8 caracteres do ID");
            return null;
        }

        if (encontradas.size() == 1) {
            return encontradas.get(0);
        }

        // Múltiplos resultados - deixar escolher
        System.out.println("\n🔍 Encontradas " + encontradas.size() + " equipes:");
        for (int i = 0; i < encontradas.size(); i++) {
            System.out.printf("%d. %s [%s]\n", 
                    (i + 1), 
                    encontradas.get(i).getNome(),
                    encontradas.get(i).getId().substring(0, 8));
        }
        
        int escolha = ConsoleUtils.lerInt("Qual equipe deseja? (1-" + encontradas.size() + "): ");
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
                    u.getId().substring(0, 8) + "...", u.getPerfil().getDisplayName());
            System.out.println("   " + "-".repeat(40));
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO do usuário (1, 2, 3...)");
        System.out.println("• Digite o LOGIN do usuário");
        System.out.println("• Digite parte do NOME do usuário");

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

        // 2. Busca por login (método original)
        Optional<User> usuarioOpt = userRepo.findByLogin(busca);
        if (usuarioOpt.isPresent()) {
            return usuarioOpt.get();
        }

        // 3. Busca por nome (contém)
        for (User u : usuarios) {
            if (u.getNomeCompleto().toLowerCase().contains(busca.toLowerCase())) {
                return u;
            }
        }

        System.out.println("❌ Usuário não encontrado!");
        return null;
    }

    private void criarEquipe() {
        ConsoleUtils.mostrarTitulo("CRIAR EQUIPE");
        String nome = ConsoleUtils.lerString("Nome da equipe: ");
        
        if (nome.trim().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("Nome da equipe é obrigatório!");
            return;
        }
        
        // Verifica se já existe uma equipe com esse nome (funcionalidade original)
        for (Team t : teamRepo.findAll()) {
            if (t.getNome().equalsIgnoreCase(nome)) {
                ConsoleUtils.mostrarMensagemErro("❌ Já existe uma equipe com esse nome!");
                return;
            }
        }

        String descricao = ConsoleUtils.lerString("Descrição (opcional): ");
        
        try {
            Team equipe = new Team(nome);
            if (!descricao.trim().isEmpty()) {
                equipe.setDescription(descricao);
            }
            
            teamRepo.save(equipe);
            
            // Log da operação
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "CREATE_TEAM", 
                              equipe.getId(), "Equipe criada: " + nome);
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Equipe criada com sucesso!");
            System.out.println("ID da equipe: " + equipe.getId().substring(0, 8) + "...");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao criar equipe: " + e.getMessage());
        }
    }

    private void listarEquipes() {
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            ConsoleUtils.mostrarTitulo("LISTA DE EQUIPES");
            System.out.println("Nenhuma equipe cadastrada.");
            return;
        }
        
        ConsoleUtils.mostrarTitulo("LISTA DE EQUIPES");
        for (int i = 0; i < equipes.size(); i++) {
            Team e = equipes.get(i);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%d. Equipe: %s\n", (i + 1), e.getNome());
            System.out.println("   ID: " + e.getId().substring(0, 8) + "...");
            
            if (e.getDescription() != null && !e.getDescription().trim().isEmpty()) {
                System.out.println("   Descrição: " + e.getDescription());
            }
            
            if (e.getMembros() == null || e.getMembros().isEmpty()) {
                System.out.println("   (Sem membros)");
            } else {
                System.out.println("   Membros (" + e.getMembros().size() + "):");
                for (User u : e.getMembros()) {
                    System.out.println("    - " + u.getNomeCompleto() + " (" + u.getPerfil().getDisplayName() + ")");
                }
            }
            System.out.println();
        }
    }

    // NOVO MÉTODO: Editar equipe
    private void editarEquipe() {
        ConsoleUtils.mostrarTitulo("EDITAR EQUIPE");

        Team equipe = selecionarEquipe();
        if (equipe == null) {
            return;
        }

        // Confirmar equipe selecionada
        System.out.println("\n✅ EQUIPE SELECIONADA:");
        System.out.println("Nome: " + equipe.getNome());
        if (equipe.getDescription() != null && !equipe.getDescription().trim().isEmpty()) {
            System.out.println("Descrição: " + equipe.getDescription());
        }
        System.out.println("Membros: " + (equipe.getMembros() != null ? equipe.getMembros().size() : 0));
        
        String confirmacao = ConsoleUtils.lerString("Confirma esta equipe? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.println("\n📝 EDIÇÃO DA EQUIPE:");
        System.out.println("(Deixe em branco para manter o valor atual)");

        String novoNome = ConsoleUtils.lerString("Novo nome: ");
        String novaDescricao = ConsoleUtils.lerString("Nova descrição: ");

        if (!novoNome.trim().isEmpty()) {
            // Verificar se novo nome já existe
            for (Team t : teamRepo.findAll()) {
                if (t.getNome().equalsIgnoreCase(novoNome) && !t.getId().equals(equipe.getId())) {
                    ConsoleUtils.mostrarMensagemErro("❌ Já existe uma equipe com esse nome!");
                    return;
                }
            }
            equipe.setName(novoNome);
            System.out.println("✓ Nome atualizado!");
        }
        
        if (!novaDescricao.trim().isEmpty()) {
            equipe.setDescription(novaDescricao);
            System.out.println("✓ Descrição atualizada!");
        }

        try {
            teamRepo.save(equipe);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "EDIT_TEAM", 
                              equipe.getId(), "Equipe editada: " + equipe.getNome());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Equipe atualizada com sucesso!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao atualizar equipe: " + e.getMessage());
        }
    }

    // MÉTODO ATUALIZADO: Adicionar membro com seleção inteligente
    private void adicionarMembro() {
        ConsoleUtils.mostrarTitulo("ADICIONAR MEMBRO À EQUIPE");
        
        // Seleção inteligente de equipe
        Team equipe = selecionarEquipe();
        if (equipe == null) {
            return;
        }

        // Confirmar equipe
        System.out.println("\n✅ EQUIPE SELECIONADA: " + equipe.getNome());
        System.out.println("Membros atuais: " + (equipe.getMembros() != null ? equipe.getMembros().size() : 0));
        
        String confirmacao = ConsoleUtils.lerString("Confirma esta equipe? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        // Seleção inteligente de usuário
        User usuario = selecionarUsuario();
        if (usuario == null) {
            return;
        }

        // Verifica se o usuário já está na equipe (funcionalidade original)
        if (equipe.getMembros() != null && equipe.getMembros().contains(usuario)) {
            ConsoleUtils.mostrarMensagemErro("❌ Usuário já está na equipe!");
            return;
        }

        // Confirmar usuário
        System.out.println("\n👤 USUÁRIO SELECIONADO:");
        System.out.println("Nome: " + usuario.getNomeCompleto());
        System.out.println("Login: " + usuario.getLogin());
        System.out.println("Perfil: " + usuario.getPerfil().getDisplayName());
        
        String confirmacaoUsuario = ConsoleUtils.lerString("Confirma este usuário? (s/n): ");
        if (!confirmacaoUsuario.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        try {
            equipe.adicionarMembro(usuario);
            teamRepo.save(equipe);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "ADD_TEAM_MEMBER", 
                              equipe.getId(), "Membro adicionado: " + usuario.getNomeCompleto());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Membro adicionado à equipe!");
            System.out.println("📋 RESUMO:");
            System.out.println("Equipe: " + equipe.getNome());
            System.out.println("Novo membro: " + usuario.getNomeCompleto() + " (" + usuario.getLogin() + ")");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao adicionar membro: " + e.getMessage());
        }
    }

    // MÉTODO ATUALIZADO: Remover membro com seleção inteligente
    private void removerMembro() {
        ConsoleUtils.mostrarTitulo("REMOVER MEMBRO DA EQUIPE");
        
        // Lista apenas equipes com membros (funcionalidade original melhorada)
        List<Team> equipes = teamRepo.findAll();
        List<Team> equipesComMembros = equipes.stream()
            .filter(t -> t.getMembros() != null && !t.getMembros().isEmpty())
            .toList();
            
        if (equipesComMembros.isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Nenhuma equipe possui membros para remover.");
            return;
        }
        
        System.out.println("=== EQUIPES COM MEMBROS ===");
        for (int i = 0; i < equipesComMembros.size(); i++) {
            Team t = equipesComMembros.get(i);
            System.out.printf("%d. %s (%d membros)\n", (i + 1), t.getNome(), t.getMembros().size());
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO da equipe (1, 2, 3...)");
        System.out.println("• Digite o NOME COMPLETO da equipe");

        String busca = ConsoleUtils.lerString("Selecione a equipe: ");
        Team equipe = encontrarEquipe(busca, equipesComMembros);
        
        if (equipe == null) {
            return;
        }

        if (equipe.getMembros() == null || equipe.getMembros().isEmpty()) {
            ConsoleUtils.mostrarMensagemErro("❌ Esta equipe não possui membros!");
            return;
        }

        // Lista membros da equipe com numeração
        System.out.println("\n=== MEMBROS DA EQUIPE " + equipe.getNome().toUpperCase() + " ===");
        List<User> membros = equipe.getMembros();
        for (int i = 0; i < membros.size(); i++) {
            User u = membros.get(i);
            System.out.printf("%d. %s (%s) - %s\n", 
                    (i + 1), 
                    u.getNomeCompleto(), 
                    u.getLogin(), 
                    u.getPerfil().getDisplayName());
        }

        System.out.println("\n🔍 FORMAS DE SELECIONAR:");
        System.out.println("• Digite o NÚMERO do membro (1, 2, 3...)");
        System.out.println("• Digite o LOGIN do usuário");

        String buscaUsuario = ConsoleUtils.lerString("Selecione o membro a remover: ");
        User usuario = null;

        // Busca por número
        try {
            int numero = Integer.parseInt(buscaUsuario);
            if (numero >= 1 && numero <= membros.size()) {
                usuario = membros.get(numero - 1);
            }
        } catch (NumberFormatException e) {
            // Busca por login
            for (User u : membros) {
                if (u.getLogin().equalsIgnoreCase(buscaUsuario)) {
                    usuario = u;
                    break;
                }
            }
        }

        if (usuario == null) {
            ConsoleUtils.mostrarMensagemErro("❌ Membro não encontrado!");
            return;
        }

        // Confirmação
        System.out.println("\n⚠️ MEMBRO A SER REMOVIDO:");
        System.out.println("Nome: " + usuario.getNomeCompleto());
        System.out.println("Login: " + usuario.getLogin());
        System.out.println("Da equipe: " + equipe.getNome());

        String confirmacao = ConsoleUtils.lerString("⚠️ CONFIRMA REMOÇÃO? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operação cancelada.");
            return;
        }

        try {
            equipe.removerMembro(usuario);
            teamRepo.save(equipe);
            
            if (authService.getCurrentUser() != null) {
                logService.log(authService.getCurrentUser().getId(), "REMOVE_TEAM_MEMBER", 
                              equipe.getId(), "Membro removido: " + usuario.getNomeCompleto());
            }
            
            ConsoleUtils.mostrarMensagemSucesso("✅ Membro removido da equipe!");
        } catch (Exception e) {
            ConsoleUtils.mostrarMensagemErro("Erro ao remover membro: " + e.getMessage());
        }
    }

    // NOVO MÉTODO: Excluir equipe
    private void excluirEquipe() {
        ConsoleUtils.mostrarTitulo("EXCLUIR EQUIPE");

        Team equipe = selecionarEquipe();
        if (equipe == null) {
            return;
        }

        // Mostrar detalhes da equipe
        System.out.println("\n🗑️ EQUIPE A SER EXCLUÍDA:");
        System.out.println("Nome: " + equipe.getNome());
        if (equipe.getDescription() != null && !equipe.getDescription().trim().isEmpty()) {
            System.out.println("Descrição: " + equipe.getDescription());
        }
        System.out.println("Membros: " + (equipe.getMembros() != null ? equipe.getMembros().size() : 0));

        String confirmacao = ConsoleUtils.lerString("⚠️ CONFIRMA EXCLUSÃO da equipe '" + 
                                                   equipe.getNome() + "'? (s/n): ");
        
        if (confirmacao.equalsIgnoreCase("s")) {
            try {
                teamRepo.delete(equipe.getId());
                
                if (authService.getCurrentUser() != null) {
                    logService.log(authService.getCurrentUser().getId(), "DELETE_TEAM", 
                                  equipe.getId(), "Equipe excluída: " + equipe.getNome());
                }
                
                ConsoleUtils.mostrarMensagemSucesso("✅ Equipe removida com sucesso!");
            } catch (Exception e) {
                ConsoleUtils.mostrarMensagemErro("Erro ao excluir equipe: " + e.getMessage());
            }
        } else {
            ConsoleUtils.mostrarMensagemSucesso("❌ Exclusão cancelada.");
        }
    }

    // NOVO MÉTODO: Estatísticas
    private void mostrarEstatisticas() {
        ConsoleUtils.mostrarTitulo("ESTATÍSTICAS DAS EQUIPES");
        
        List<Team> equipes = teamRepo.findAll();
        if (equipes.isEmpty()) {
            System.out.println("Nenhuma equipe cadastrada.");
            return;
        }

        System.out.println("=== ESTATÍSTICAS ===");
        System.out.println("Total de equipes: " + equipes.size());
        
        int totalMembros = 0;
        int equipesComMembros = 0;
        
        for (Team t : equipes) {
            if (t.getMembros() != null && !t.getMembros().isEmpty()) {
                totalMembros += t.getMembros().size();
                equipesComMembros++;
            }
        }
        
        System.out.println("Total de membros em equipes: " + totalMembros);
        System.out.println("Equipes com membros: " + equipesComMembros);
        System.out.println("Equipes vazias: " + (equipes.size() - equipesComMembros));
        
        if (equipesComMembros > 0) {
            double media = (double) totalMembros / equipesComMembros;
            System.out.printf("Média de membros por equipe ativa: %.1f\n", media);
            
            // Maior equipe
            Team maiorEquipe = equipes.stream()
                    .filter(t -> t.getMembros() != null)
                    .max((t1, t2) -> Integer.compare(t1.getMembros().size(), t2.getMembros().size()))
                    .orElse(null);
                    
            if (maiorEquipe != null && maiorEquipe.getMembros().size() > 0) {
                System.out.println("Maior equipe: " + maiorEquipe.getNome() + 
                                 " (" + maiorEquipe.getMembros().size() + " membros)");
            }
        }
    }
}