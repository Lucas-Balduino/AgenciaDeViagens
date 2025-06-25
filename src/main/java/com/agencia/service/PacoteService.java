package com.agencia.service;

import com.agencia.dao.PacoteDao;
import com.agencia.model.Servico;
import com.agencia.dao.ClienteDao; 
import com.agencia.dao.ServicoDao;
import com.agencia.model.Pacote;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; 
import java.util.stream.Collectors; 
import java.sql.SQLException; // Import necessário para SQLException

public class PacoteService {
	private PacoteDao pacoteDao = new PacoteDao();
	private ServicoDao servicoDao = new ServicoDao();
    private Scanner scanner; 

    public PacoteService(Scanner scanner) {
        this.scanner = scanner;
    }
    
    public PacoteService() {
    }

    /**
     * Retorna uma lista de todos os pacotes. Ideal para uso em GUI.
     *
     * @return Lista de objetos Pacote.
     */
    public List<Pacote> buscarTodosPacotesGUI() {
        try {
            return pacoteDao.buscarTodos();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar todos os pacotes para GUI: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retorna um pacote específico pelo ID. Ideal para uso em GUI.
     *
     * @param id O ID do pacote.
     * @return O objeto Pacote, ou null se não encontrado ou ocorrer erro.
     */
    public Pacote buscarPacotePorIdGUI(Long id) {
        try {
            return pacoteDao.buscarPorId(id);
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pacote por ID para GUI: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retorna a lista de serviços associados a um pacote específico. Ideal para uso em GUI.
     *
     * @param pacoteId O ID do pacote.
     * @return Lista de objetos Servico associados ao pacote.
     */
    public List<Servico> buscarServicosDoPacoteGUI(Long pacoteId) {
        try {
            return pacoteDao.buscarServicosDoPacote(pacoteId);
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviços do pacote para GUI: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Retorna a lista de serviços NÃO associados a um pacote específico. Ideal para uso em GUI.
     *
     * @param pacoteId O ID do pacote.
     * @return Lista de objetos Servico não associados ao pacote.
     */
    public List<Servico> buscarServicosNaoAssociadosAoPacoteGUI(Long pacoteId) {
        try {
            List<Servico> todosServicos = servicoDao.buscarTodos();
            List<Servico> servicosDoPacote = pacoteDao.buscarServicosDoPacote(pacoteId);

            return todosServicos.stream()
                    .filter(s -> servicosDoPacote.stream().noneMatch(sp -> sp.getId().equals(s.getId())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviços não associados ao pacote para GUI: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Adiciona um serviço a um pacote. Ideal para uso em GUI.
     *
     * @param pacoteId O ID do pacote.
     * @param servicoId O ID do serviço a ser adicionado.
     * @throws Exception Se ocorrer um erro ou o serviço já estiver no pacote.
     */
    public void adicionarServicoAoPacoteGUI(Long pacoteId, Long servicoId) throws Exception {
        if (pacoteId == null || pacoteId <= 0 || servicoId == null || servicoId <= 0) {
            throw new IllegalArgumentException("IDs de pacote ou serviço inválidos.");
        }
        try {
            pacoteDao.adicionarServicoAoPacote(pacoteId, servicoId);
        } catch (Exception e) {
            throw new Exception("Falha ao adicionar serviço ao pacote: " + e.getMessage(), e);
        }
    }

    /**
     * Remove um serviço de um pacote. Ideal para uso em GUI.
     *
     * @param pacoteId O ID do pacote.
     * @param servicoId O ID do serviço a ser removido.
     * @throws Exception Se ocorrer um erro.
     */
    public void removerServicoDoPacoteGUI(Long pacoteId, Long servicoId) throws Exception {
        if (pacoteId == null || pacoteId <= 0 || servicoId == null || servicoId <= 0) {
            throw new IllegalArgumentException("IDs de pacote ou serviço inválidos.");
        }
        try {
            pacoteDao.removerServicoDoPacote(pacoteId, servicoId);
        } catch (Exception e) {
            throw new Exception("Falha ao remover serviço do pacote: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza os dados de um pacote existente (Nome, Destino, Duração, Preço).
     *
     * @param pacote O objeto Pacote com os dados atualizados, incluindo o ID.
     * @throws Exception Se ocorrer um erro durante a atualização.
     */
    public void atualizarPacoteGUI(Pacote pacote) throws Exception {
        if (pacote == null || pacote.getId() == null || pacote.getId() <= 0) {
            throw new IllegalArgumentException("Pacote ou ID do pacote inválido para atualização.");
        }
        if (pacote.getNome() == null || pacote.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do pacote não pode ser vazio.");
        }
        if (pacote.getDestino() == null || pacote.getDestino().trim().isEmpty()) {
            throw new IllegalArgumentException("O destino do pacote não pode ser vazio.");
        }
        if (pacote.getDuracao() == null || pacote.getDuracao() <= 0) {
            throw new IllegalArgumentException("A duração do pacote deve ser um número inteiro maior que zero.");
        }
        if (pacote.getPreco() == null || pacote.getPreco() <= 0) {
            throw new IllegalArgumentException("O preço do pacote deve ser maior que zero.");
        }

        try {
            pacoteDao.atualizar(pacote);
        } catch (SQLException e) {
            throw new Exception("Falha ao atualizar pacote no banco de dados: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar pacote: " + e.getMessage(), e);
        }
    }


    // --- Métodos originais que usam Scanner (mantidos para compatibilidade com Main.java) ---
    public void cadastrarPacote() {
        System.out.println("\n=== CADASTRAR PACOTE ===");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Destino: ");
        String destino = scanner.nextLine();

        if (destino == null || destino.trim().isEmpty()) {
            System.out.println("❌ Destino não pode ser vazio. Cadastro cancelado.");
            return;
        }

        System.out.print("Duração (dias): ");
        int duracao = scanner.nextInt();

        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine();

        if (preco <= 0) {
            System.out.println("❌ Preço deve ser maior que 0. Cadastro cancelado.");
            return;
        }

        try {
            Pacote pacote = new Pacote(nome, destino, duracao, preco);
            pacoteDao.inserir(pacote);
            System.out.println("✅ Pacote cadastrado!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar: " + e.getMessage());
        }
    }

    public void listarTodosPacotesComServicos() {
        try {
            var pacotes = pacoteDao.buscarTodos();

            if (pacotes.isEmpty()) {
                System.out.println("📭 Nenhum pacote encontrado.");
                return;
            }

            for (Pacote pacote : pacotes) {
                System.out.println("\n[" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
                System.out.println("Serviços contratados:");

                var servicos = pacoteDao.buscarServicosDoPacote(pacote.getId());

                if (servicos.isEmpty()) {
                    System.out.println("Nenhum serviço associado a este pacote.");
                } else {
                    for (var servico : servicos) {
                        System.out.println(servico.getNome());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao listar pacotes: " + e.getMessage());
        }
    }
    
    public void buscarPacotePorId() {
        System.out.print("Digite o ID do pacote: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try {
            Pacote pacote = pacoteDao.buscarPorId(id);

            if (pacote == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            System.out.println("\n📦 Pacote [" + pacote.getId() + "]");
            System.out.println("Nome: " + pacote.getNome());
            System.out.println("Destino: " + pacote.getDestino());
            System.out.println("Duração: " + pacote.getDuracao() + " dias");
            System.out.println("Preço: R$" + pacote.getPreco());

            var servicos = pacoteDao.buscarServicosDoPacote(id);

            if (servicos.isEmpty()) {
                System.out.println("Serviços inclusos: Nenhum.");
            } else {
                for (var servico : servicos) {
                    System.out.println("- " + servico.getNome() + " (" + servico.getDuracao() + " dias)");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pacote: " + e.getMessage());
        }
    }
    
    public void deletarPacote() {
        System.out.print("Digite o ID do pacote para deletar: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        try {
            Pacote pacote = pacoteDao.buscarPorId(id);

            if (pacote == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            if (pacoteDao.pacoteEstaContratado(id)) {
                System.out.println("❌ Este pacote está contratado por um ou mais clientes. Não pode ser deletado.");
                return;
            }

            pacoteDao.deletar(id);
            System.out.println("✅ Pacote deletado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao deletar pacote: " + e.getMessage());
        }
    }


    public void contratarServicoParaPacote() { 
        try {
            System.out.println("\n=== CONTRATAR SERVIÇO PARA PACOTE ===");

            var pacotes = pacoteDao.buscarTodos();

            if (pacotes.isEmpty()) {
                System.out.println("📭 Nenhum pacote cadastrado.");
                return;
            }

            System.out.println("\n📦 Pacotes disponíveis:");
            for (var pacote : pacotes) {
                System.out.println("[" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
            }

            System.out.print("\nDigite o ID do pacote: ");
            Long pacoteId = scanner.nextLong();
            scanner.nextLine();

            var pacoteSelecionado = pacoteDao.buscarPorId(pacoteId);

            if (pacoteSelecionado == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            var servicosDoPacote = pacoteDao.buscarServicosDoPacote(pacoteId);
            var todosServicos = servicoDao.buscarTodos();

            List<Servico> servicosDisponiveis = new ArrayList<>();
            for (Servico servico : todosServicos) {
                boolean jaContratado = false;
                for (Servico contratado : servicosDoPacote) {
                    if (servico.getId().equals(contratado.getId())) {
                        jaContratado = true;
                        break;
                    }
                }
                if (!jaContratado) {
                    servicosDisponiveis.add(servico);
                }
            }

            if (servicosDisponiveis.isEmpty()) {
                System.out.println("📭 Não há serviços disponíveis para adicionar neste pacote.");
                return;
            }

            System.out.println("\n🛠️ Serviços disponíveis para adicionar:");
            for (var servico : servicosDisponiveis) {
                System.out.println("[" + servico.getId() + "] " + servico.getNome() + " (" + servico.getDuracao() + " dias)");
            }

            System.out.print("\nDigite o ID do serviço que deseja adicionar: ");
            Long servicoId = scanner.nextLong();
            scanner.nextLine();

            boolean valido = false;
            for (var servico : servicosDisponiveis) {
                if (servico.getId().equals(servicoId)) {
                    valido = true;
                    break;
                }
            }

            if (!valido) {
                System.out.println("❌ Serviço inválido ou já contratado para o pacote.");
                return;
            }

            adicionarServicoAoPacoteGUI(pacoteId, servicoId); 
            System.out.println("✅ Serviço adicionado ao pacote com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao contratar serviço para pacote: " + e.getMessage());
        }
    }

    public void listarClientesPorPacote() {
        try {
            System.out.println("\n=== LISTAR CLIENTES POR PACOTE ===");
            System.out.print("Digite o ID do pacote: ");
            Long pacoteId = scanner.nextLong();
            scanner.nextLine();

            Pacote pacote = pacoteDao.buscarPorId(pacoteId);

            if (pacote == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            ClienteDao clienteDao = new ClienteDao();
            var nacionais = clienteDao.buscarNacionaisPorPacote(pacoteId);
            var estrangeiros = clienteDao.buscarEstrangeirosPorPacote(pacoteId);

            if (nacionais.isEmpty() && estrangeiros.isEmpty()) {
                System.out.println("📭 Nenhum cliente contratou este pacote ainda.");
                return;
            }

            System.out.println("\n📦 Pacote: " + pacote.getNome() + " - " + pacote.getDestino());

            if (!nacionais.isEmpty()) {
                System.out.println("\nClientes Nacionais:");
                for (var nacional : nacionais) {
                    System.out.println("- " + nacional.getNome() + " (CPF: " + nacional.getCpf() + ")");
                }
            }

            if (!estrangeiros.isEmpty()) {
                System.out.println("\nClientes Estrangeiros:");
                for (var estrangeiro : estrangeiros) {
                    System.out.println("- " + estrangeiro.getNome() + " (Passaporte: " + estrangeiro.getPassaporte() + ")");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao listar clientes: " + e.getMessage());
        }
    }
    
    public List<Pacote> buscarTodosPacotes() { // Este método já existe e é usado pelo ClienteService
        try {
            return pacoteDao.buscarTodos();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pacotes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
