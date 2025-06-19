package com.agencia;

import java.sql.Connection;
import java.util.Scanner;

import com.agencia.util.DataBaseConnection;
import com.agencia.service.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ClienteService clienteService = new ClienteService(scanner);
        PacoteService pacoteService = new PacoteService(scanner);
        ServicoService servicoService = new ServicoService(scanner);
        Connection conn = null;

        try {
            conn = DataBaseConnection.getConnection();
            System.out.println("✅ Conexão estabelecida com sucesso!");
            boolean executando = true;

            while (executando) {
                System.out.println("\n===== MENU PRINCIPAL =====");
                System.out.println("[1] Cadastrar");
                System.out.println("[2] Remover");
                System.out.println("[3] Listar");
                System.out.println("[4] Buscar");
                System.out.println("[5] Contratar");
                System.out.println("[6] Fechar programa");
                System.out.print("Escolha uma opção: ");
                int opcao = scanner.nextInt();

                switch (opcao) {
                    case 1:
                    	clearConsole();
                        menuSecundario("Cadastrar", clienteService, pacoteService, servicoService, scanner);
                        break;
                    case 2:
                    	clearConsole();
                        menuSecundario("Remover", clienteService, pacoteService, servicoService, scanner);
                        break;
                    case 3:
                    	clearConsole();
                        menuSecundario("Listar", clienteService, pacoteService, servicoService, scanner);
                        break;
                    case 4:
                    	clearConsole();
                        menuSecundario("Buscar", clienteService, pacoteService, servicoService, scanner);
                        break;
                    case 5:
                    	clearConsole();
                        menuContratar(scanner); // Passar o scanner existente
                        break;
                    case 6:
                    	clearConsole();
                        executando = false;
                        System.out.println("👋 Programa finalizado.");
                        break;
                    default:
                        System.out.println("❌ Opção inválida. Tente novamente.");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erro durante a execução: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DataBaseConnection.desconectar(conn);
            scanner.close();
        }
    }

    private static void menuSecundario(String acao, ClienteService clienteService, PacoteService pacoteService, ServicoService servicoService, Scanner scanner) {
        boolean voltar = false;

        while (!voltar) {
            System.out.println("\n=== " + acao.toUpperCase() + " ===");
            System.out.println("[1] Cliente");
            System.out.println("[2] Pacote");
            System.out.println("[3] Serviço");
            System.out.println("[4] Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            int escolha = scanner.nextInt();
            scanner.nextLine(); // limpa o buffer

            switch (escolha) {
            case 1:
            	clearConsole();
                if (acao.equalsIgnoreCase("Cadastrar")) {
                    clienteService.cadastrarCliente();
                } else if (acao.equalsIgnoreCase("Buscar")) {
                    clienteService.buscarCliente();
                } else if (acao.equalsIgnoreCase("Listar")) {
                    clienteService.listarTodosClientes();
                } else if (acao.equalsIgnoreCase("Remover")) {
                    clienteService.removerCliente();
                } else {
                    System.out.println("⚙️ Função de " + acao.toLowerCase() + " cliente ainda será implementada.");
                }
                break;
            case 2:
            	clearConsole();
                if (acao.equalsIgnoreCase("Cadastrar")) {
                    pacoteService.cadastrarPacote();
                } else if (acao.equalsIgnoreCase("Listar")) {
                    pacoteService.listarTodosPacotesComServicos();
                } else if (acao.equalsIgnoreCase("Buscar")) {
                    System.out.println("\n=== BUSCAR PACOTE ===");
                    System.out.println("[1] Buscar Pacote por ID");
                    System.out.println("[2] Listar Clientes que contrataram Pacote");
                    System.out.println("[3] Voltar");
                    System.out.print("Escolha uma opção: ");
                    int subOpcao = scanner.nextInt();
                    scanner.nextLine(); // limpar buffer

                    switch (subOpcao) {
                        case 1:
                        	clearConsole();
                            pacoteService.buscarPacotePorId();
                            break;
                        case 2:
                        	clearConsole();
                            pacoteService.listarClientesPorPacote();
                            break;
                        case 3:
                            return;
                        default:
                            System.out.println("❌ Opção inválida.");
                    }

                } else if (acao.equalsIgnoreCase("Remover")) {
                    pacoteService.deletarPacote();
                } else {
                    System.out.println("⚙️ Função de " + acao.toLowerCase() + " pacote ainda será implementada.");
                }
                break;
            case 3:
            	clearConsole();
                if (acao.equalsIgnoreCase("Cadastrar")) {
                    servicoService.cadastrarServico();
                } else if (acao.equalsIgnoreCase("Listar")) {
                    servicoService.listarTodosServicos();
                } else if (acao.equalsIgnoreCase("Buscar")) {
                    servicoService.buscarServicoPorId();
                } else if (acao.equalsIgnoreCase("Remover")) {
                    servicoService.removerServico();
                } else {
                    System.out.println("⚙️ Função de " + acao.toLowerCase() + " serviço ainda será implementada.");
                }
                break;

            case 4:
            	clearConsole();
                voltar = true;
                break;
            default:
                System.out.println("❌ Opção inválida.");
        }



        }
    }


    private static void menuContratar(Scanner scanner) {
        ClienteService clienteService = new ClienteService(scanner);
        PacoteService pacoteService = new PacoteService(scanner);
        boolean voltar = false;

        while (!voltar) {
            System.out.println("\n=== CONTRATAR ===");
            System.out.println("[1] Pacotes (Cliente contratar Pacote)");
            System.out.println("[2] Serviços (Adicionar Serviço a Pacote)");
            System.out.println("[3] Voltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            int escolha = scanner.nextInt();
            scanner.nextLine(); // limpar buffer

            switch (escolha) {
                case 1:
                	clearConsole();
                    clienteService.contratarPacote();
                    break;
                case 2:
                	clearConsole();
                    pacoteService.contratarServicoParaPacote();
                    break;
                case 3:
                	clearConsole();
                    voltar = true;
                    break;
                default:
                    System.out.println("❌ Opção inválida.");
            }
        }
    }
    
    public static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

}
