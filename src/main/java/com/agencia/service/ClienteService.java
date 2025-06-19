package com.agencia.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.agencia.dao.ClienteDao;
import com.agencia.model.Pacote;
import com.agencia.model.Cliente;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Nacional;

public class ClienteService {

    private ClienteDao clienteDao = new ClienteDao();
    private Scanner scanner;
    
    public ClienteService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void cadastrarCliente() {
        System.out.println("🧾 Cadastrar cliente:");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("O cliente é nacional ou estrangeiro? (n/e): ");
        String tipo = scanner.nextLine().trim().toLowerCase();

        try {
            Cliente cliente = null;

            if (tipo.equals("n")) {
                System.out.print("CPF: ");
                String cpf = scanner.nextLine();
                cliente = new Nacional(nome, telefone, email, cpf);
            } else if (tipo.equals("e")) {
                System.out.print("Passaporte: ");
                String passaporte = scanner.nextLine();
                cliente = new Estrangeiro(nome, telefone, email, passaporte);
            } else {
                System.out.println("❌ Tipo inválido.");
                return;
            }

            clienteDao.inserir(cliente);
            System.out.println("✅ Cliente cadastrado com sucesso!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar cliente: " + e.getMessage());
        }
    }
    
    public void buscarCliente() {
        System.out.print("O cliente é nacional ou estrangeiro? (n/e): ");
        String tipo = scanner.nextLine().trim().toLowerCase();

        try {
            if (tipo.equals("n")) {
                System.out.print("Digite o CPF: ");
                String cpf = scanner.nextLine();

                Nacional cliente = clienteDao.buscarNacionalPorCpf(cpf);
                if (cliente != null) {
                    System.out.println("\n✅ Cliente encontrado:");
                    System.out.println(cliente);

                    // Mostrar pacotes contratados
                    var pacotes = clienteDao.buscarPacotesContratadosNacional(cliente.getId());

                    if (pacotes.isEmpty()) {
                        System.out.println("\n📦 Nenhum pacote contratado.");
                    } else {
                        System.out.println("\n📦 Pacotes contratados:");
                        for (var pacote : pacotes) {
                            System.out.println("- [" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
                        }
                    }

                } else {
                    System.out.println("❌ Cliente não encontrado.");
                }

            } else if (tipo.equals("e")) {
                System.out.print("Digite o passaporte: ");
                String passaporte = scanner.nextLine();

                Estrangeiro cliente = clienteDao.buscarEstrangeiroPorPassaporte(passaporte);
                if (cliente != null) {
                    System.out.println("\n✅ Cliente encontrado:");
                    System.out.println(cliente);

                    // Mostrar pacotes contratados
                    var pacotes = clienteDao.buscarPacotesContratadosEstrangeiro(cliente.getId());

                    if (pacotes.isEmpty()) {
                        System.out.println("\n📦 Nenhum pacote contratado.");
                    } else {
                        System.out.println("\n📦 Pacotes contratados:");
                        for (var pacote : pacotes) {
                            System.out.println("- [" + pacote.getId() + "] " + pacote.getNome() + " - " + pacote.getDestino());
                        }
                    }

                } else {
                    System.out.println("❌ Cliente não encontrado.");
                }

            } else {
                System.out.println("❌ Tipo inválido.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar cliente: " + e.getMessage());
        }
    }

    
    public void listarTodosClientes() {
        try {
            clienteDao.listarTodosClientesSimples();
        } catch (Exception e) {
            System.err.println("❌ Erro ao listar clientes: " + e.getMessage());
        }
    }
    
    public void removerCliente() {
    	
        System.out.print("O cliente é nacional ou estrangeiro? (n/e): ");
        String tipo = scanner.nextLine().trim().toLowerCase();

        try {
            if (tipo.equals("n")) {
                System.out.print("Digite o CPF: ");
                String cpf = scanner.nextLine();
                clienteDao.removerClientePorDocumento(cpf, "n");

            } else if (tipo.equals("e")) {
                System.out.print("Digite o Passaporte: ");
                String passaporte = scanner.nextLine();
                clienteDao.removerClientePorDocumento(passaporte, "e");

            } else {
                System.out.println("❌ Tipo inválido.");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover cliente: " + e.getMessage());
        }
    }

    public void contratarPacote() {
        System.out.print("O cliente é nacional ou estrangeiro? (n/e): ");
        String tipo = scanner.nextLine().trim().toLowerCase();

        try {
            if (tipo.equals("n")) {
                System.out.print("Digite o CPF do cliente: ");
                String cpf = scanner.nextLine();
                Nacional cliente = clienteDao.buscarNacionalPorCpf(cpf);

                if (cliente == null) {
                    System.out.println("❌ Cliente não encontrado.");
                    return;
                }

                PacoteService pacoteService = new PacoteService(scanner);
                var todosPacotes = pacoteService.buscarTodosPacotes();
                var pacotesContratados = clienteDao.buscarPacotesContratadosNacional(cliente.getId());

                List<Pacote> pacotesDisponiveis = new ArrayList<>();

                for (Pacote pacote : todosPacotes) {
                    boolean jaContratado = false;
                    for (Pacote contratado : pacotesContratados) {
                        if (pacote.getId().equals(contratado.getId())) {
                            jaContratado = true;
                            break;
                        }
                    }
                    if (!jaContratado) {
                        pacotesDisponiveis.add(pacote);
                    }
                }

                if (pacotesDisponiveis.isEmpty()) {
                    System.out.println("📭 Nenhum pacote disponível para contratação.");
                    return;
                }

                System.out.println("\n📦 Pacotes disponíveis:");
                for (Pacote pacote : pacotesDisponiveis) {
                    System.out.println("[" + pacote.getId() + "] " + pacote.getNome() +
                                       " - Destino: " + pacote.getDestino() +
                                       " - Duração: " + pacote.getDuracao() + " dias" +
                                       " - Preço: R$" + pacote.getPreco());
                }

                System.out.print("\nDigite o ID do pacote que deseja contratar: ");
                Long pacoteId = scanner.nextLong();
                scanner.nextLine(); // limpar buffer

                // Confirmar que o pacote escolhido é realmente válido
                boolean pacoteValido = false;
                for (Pacote pacote : pacotesDisponiveis) {
                    if (pacote.getId().equals(pacoteId)) {
                        pacoteValido = true;
                        break;
                    }
                }

                if (!pacoteValido) {
                    System.out.println("❌ Pacote inválido ou já contratado!");
                    return;
                }

                clienteDao.contratarPacoteNacional(cliente.getId(), pacoteId);
                System.out.println("✅ Pacote contratado para cliente nacional!");

            } else if (tipo.equals("e")) {
                System.out.print("Digite o passaporte do cliente: ");
                String passaporte = scanner.nextLine();
                Estrangeiro cliente = clienteDao.buscarEstrangeiroPorPassaporte(passaporte);

                if (cliente == null) {
                    System.out.println("❌ Cliente não encontrado.");
                    return;
                }

                PacoteService pacoteService = new PacoteService(scanner);
                var todosPacotes = pacoteService.buscarTodosPacotes();
                var pacotesContratados = clienteDao.buscarPacotesContratadosEstrangeiro(cliente.getId());

                List<Pacote> pacotesDisponiveis = new ArrayList<>();

                for (Pacote pacote : todosPacotes) {
                    boolean jaContratado = false;
                    for (Pacote contratado : pacotesContratados) {
                        if (pacote.getId().equals(contratado.getId())) {
                            jaContratado = true;
                            break;
                        }
                    }
                    if (!jaContratado) {
                        pacotesDisponiveis.add(pacote);
                    }
                }

                if (pacotesDisponiveis.isEmpty()) {
                    System.out.println("📭 Nenhum pacote disponível para contratação.");
                    return;
                }

                System.out.println("\n📦 Pacotes disponíveis:");
                for (Pacote pacote : pacotesDisponiveis) {
                    System.out.println("[" + pacote.getId() + "] " + pacote.getNome() +
                                       " - Destino: " + pacote.getDestino() +
                                       " - Duração: " + pacote.getDuracao() + " dias" +
                                       " - Preço: R$" + pacote.getPreco());
                }

                System.out.print("\nDigite o ID do pacote que deseja contratar: ");
                Long pacoteId = scanner.nextLong();
                scanner.nextLine(); // limpar buffer

                boolean pacoteValido = false;
                for (Pacote pacote : pacotesDisponiveis) {
                    if (pacote.getId().equals(pacoteId)) {
                        pacoteValido = true;
                        break;
                    }
                }

                if (!pacoteValido) {
                    System.out.println("❌ Pacote inválido ou já contratado!");
                    return;
                }

                clienteDao.contratarPacoteEstrangeiro(cliente.getId(), pacoteId);
                System.out.println("✅ Pacote contratado para cliente estrangeiro!");

            } else {
                System.out.println("❌ Tipo inválido.");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao contratar pacote: " + e.getMessage());
        }
    }


}
