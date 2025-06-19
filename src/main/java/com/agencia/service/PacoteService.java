package com.agencia.service;

import com.agencia.dao.PacoteDao;
import com.agencia.model.Servico;
import com.agencia.dao.ClienteDao;
import com.agencia.dao.ServicoDao;
import com.agencia.model.Pacote;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PacoteService {
	private PacoteDao pacoteDao = new PacoteDao();
	private ServicoDao servicoDao = new ServicoDao();
    private Scanner scanner;

    public PacoteService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarPacote() {
        System.out.println("\n=== CADASTRAR PACOTE ===");

        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Destino: ");
        String destino = scanner.nextLine();

        // Validação destino
        if (destino == null || destino.trim().isEmpty()) {
            System.out.println("❌ Destino não pode ser vazio. Cadastro cancelado.");
            return;
        }

        System.out.print("Duração (dias): ");
        int duracao = scanner.nextInt();

        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        scanner.nextLine(); // Limpar buffer

        // Validação preço
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
            // Buscar todos os pacotes
            var pacotes = pacoteDao.buscarTodos();

            if (pacotes.isEmpty()) {
                System.out.println("📭 Nenhum pacote encontrado.");
                return;
            }

            for (Pacote pacote : pacotes) {
                System.out.println("\n[" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
                System.out.println("Serviços contratados:");

                // Buscar os serviços de cada pacote
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
        scanner.nextLine(); // limpar buffer

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
                System.out.println("Serviços inclusos:");
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
        scanner.nextLine(); // limpar buffer

        try {
            Pacote pacote = pacoteDao.buscarPorId(id);

            if (pacote == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            // Verificar se está contratado
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

            // 1. Mostrar todos os pacotes
            var pacotes = pacoteDao.buscarTodos();

            if (pacotes.isEmpty()) {
                System.out.println("📭 Nenhum pacote cadastrado.");
                return;
            }

            System.out.println("\n📦 Pacotes disponíveis:");
            for (var pacote : pacotes) {
                System.out.println("[" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
            }

            // 2. Escolher o pacote
            System.out.print("\nDigite o ID do pacote: ");
            Long pacoteId = scanner.nextLong();
            scanner.nextLine(); // limpar buffer

            var pacoteSelecionado = pacoteDao.buscarPorId(pacoteId);

            if (pacoteSelecionado == null) {
                System.out.println("❌ Pacote não encontrado.");
                return;
            }

            // 3. Mostrar apenas serviços ainda não contratados
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

            // 4. Escolher o serviço
            System.out.print("\nDigite o ID do serviço que deseja adicionar: ");
            Long servicoId = scanner.nextLong();
            scanner.nextLine(); // limpar buffer

            // Verifica se o serviço escolhido realmente está disponível
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

            // 5. Adicionar o serviço
            pacoteDao.adicionarServicoAoPacote(pacoteId, servicoId);

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
            scanner.nextLine(); // limpar buffer

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
    
    public List<Pacote> buscarTodosPacotes() {
        try {
            return pacoteDao.buscarTodos();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar pacotes: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    
}