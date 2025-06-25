package com.agencia.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Mantido para compatibilidade com a versão de console

import com.agencia.dao.ClienteDao;
import com.agencia.model.Pacote;
import com.agencia.model.Cliente;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Nacional;

public class ClienteService {

    private ClienteDao clienteDao = new ClienteDao();
    private Scanner scanner; // A instância do scanner é usada apenas nos métodos de console

    // Construtor sem scanner, para uso em GUIs
    public ClienteService() {
        // Inicializa o ClienteDao
    }

    // Construtor com scanner, mantido para a compatibilidade com a Main.java de console
    public ClienteService(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Busca um cliente por documento (CPF ou Passaporte) para uso em GUI.
     * Retorna o objeto Cliente (Nacional ou Estrangeiro) se encontrado, ou null.
     *
     * @param documento O CPF ou Passaporte do cliente.
     * @param isNacional True se for um cliente nacional, False se for estrangeiro.
     * @return O objeto Cliente encontrado, ou null se não for encontrado ou ocorrer erro.
     * @throws Exception Se ocorrer um erro durante a busca no banco de dados.
     */
    public Cliente buscarClientePorDocumentoGUI(String documento, boolean isNacional) throws Exception {
        if (documento == null || documento.trim().isEmpty()) {
            return null; 
        }
        try {
            if (isNacional) {
                return clienteDao.buscarNacionalPorCpf(documento);
            } else {
                return clienteDao.buscarEstrangeiroPorPassaporte(documento);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao buscar cliente por documento: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna uma lista de todos os clientes (nacionais e estrangeiros) aplicando um filtro de nome/documento.
     * Ideal para uso em GUIs para popular tabelas.
     * @param filtro Texto para filtrar clientes por nome, CPF ou passaporte.
     * @return Uma lista de objetos Cliente.
     * @throws Exception Se ocorrer um erro durante a busca.
     */
    public List<Cliente> buscarTodosClientesGUI(String filtro) throws Exception {
        try {
            return clienteDao.buscarTodosClientes(filtro); // Chama o método atualizado do DAO
        } catch (Exception e) {
            throw new Exception("Erro ao buscar todos os clientes: " + e.getMessage(), e);
        }
    }


    /**
     * Contrata um pacote para um cliente específico, via ID do cliente e do pacote.
     * Adaptado para uso em GUI, sem interação com console.
     *
     * @param clienteId O ID do cliente (Nacional ou Estrangeiro).
     * @param isNacional True se o cliente for nacional, False se for estrangeiro.
     * @param pacoteId O ID do pacote a ser contratado.
     * @throws Exception Se o cliente ou pacote não forem encontrados, ou se houver erro no banco de dados.
     */
    public void contratarPacoteParaClienteGUI(Long clienteId, boolean isNacional, Long pacoteId) throws Exception {
        if (clienteId == null || clienteId <= 0) {
            throw new IllegalArgumentException("ID do cliente inválido.");
        }
        if (pacoteId == null || pacoteId <= 0) {
            throw new IllegalArgumentException("ID do pacote inválido.");
        }

        try {
            // Verificar se o cliente já contratou este pacote
            List<Pacote> pacotesContratados;
            if (isNacional) {
                pacotesContratados = clienteDao.buscarPacotesContratadosNacional(clienteId);
            } else {
                pacotesContratados = clienteDao.buscarPacotesContratadosEstrangeiro(clienteId);
            }

            for (Pacote p : pacotesContratados) {
                if (p.getId().equals(pacoteId)) {
                    throw new IllegalArgumentException("Este cliente já contratou este pacote.");
                }
            }

            if (isNacional) {
                clienteDao.contratarPacoteNacional(clienteId, pacoteId);
            } else {
                clienteDao.contratarPacoteEstrangeiro(clienteId, pacoteId);
            }
        } catch (SQLException e) { 
             if (e.getMessage().contains("Duplicate entry") || e.getErrorCode() == 1062) { 
                 throw new Exception("Falha ao contratar pacote: O cliente já possui este pacote. " + e.getMessage(), e);
             }
            throw new Exception("Falha ao contratar pacote para cliente (Erro no banco de dados): " + e.getMessage(), e);
        } catch (Exception e) {
            throw new Exception("Falha ao contratar pacote para cliente: " + e.getMessage(), e);
        }
    }

    /**
     * Remove um pacote contratado de um cliente. Ideal para uso em GUI.
     *
     * @param clienteId O ID do cliente.
     * @param isNacional True se o cliente for nacional, False se for estrangeiro.
     * @param pacoteId O ID do pacote a ser removido.
     * @throws Exception Se ocorrer um erro.
     */
    public void removerPacoteContratadoGUI(Long clienteId, boolean isNacional, Long pacoteId) throws Exception {
        if (clienteId == null || clienteId <= 0 || pacoteId == null || pacoteId <= 0) {
            throw new IllegalArgumentException("IDs de cliente ou pacote inválidos.");
        }
        try {
            if (isNacional) {
                clienteDao.removerPacoteNacional(clienteId, pacoteId); 
            } else {
                clienteDao.removerPacoteEstrangeiro(clienteId, pacoteId); 
            }
        } catch (Exception e) {
            throw new Exception("Falha ao remover pacote contratado: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza os dados de um cliente (nome, telefone, email).
     * O documento (CPF/Passaporte) é usado como identificador e não é alterado.
     *
     * @param cliente O objeto Cliente com os dados atualizados.
     * @return True se a atualização foi bem-sucedida, False caso contrário.
     * @throws Exception Se ocorrer um erro durante a atualização no banco de dados.
     */
    public boolean atualizarClienteGUI(Cliente cliente) throws Exception {
        if (cliente == null || cliente.getId() == null) {
            throw new IllegalArgumentException("Cliente ou ID do cliente inválido para atualização.");
        }
        try {
            return clienteDao.atualizar(cliente); // Chama o método atualizar do DAO
        } catch (Exception e) {
            throw new Exception("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }


    /**
     * Retorna a lista de pacotes contratados por um cliente para uso em GUI.
     *
     * @param clienteId O ID do cliente.
     * @param isNacional True se o cliente for nacional, False se for estrangeiro.
     * @return Uma lista de pacotes contratados.
     * @throws Exception Se ocorrer um erro durante a busca.
     */
    public List<Pacote> buscarPacotesContratadosGUI(Long clienteId, boolean isNacional) throws Exception {
        try {
            if (isNacional) {
                return clienteDao.buscarPacotesContratadosNacional(clienteId);
            } else {
                return clienteDao.buscarPacotesContratadosEstrangeiro(clienteId);
            }
        } catch (Exception e) {
            throw new Exception("Erro ao buscar pacotes contratados para cliente: " + e.getMessage(), e);
        }
    }


    // --- Métodos originais que usam Scanner (mantidos para compatibilidade com Main.java) ---
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
            boolean isNacional = tipo.equals("n");
            System.out.print("Digite o " + (isNacional ? "CPF" : "passaporte") + ": ");
            String documento = scanner.nextLine();

            Cliente cliente = buscarClientePorDocumentoGUI(documento, isNacional);
            if (cliente != null) {
                System.out.println("\n✅ Cliente encontrado:");
                System.out.println(cliente);

                // Mostrar pacotes contratados
                var pacotes = buscarPacotesContratadosGUI(cliente.getId(), isNacional);

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

        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar cliente: " + e.getMessage());
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
            boolean isNacional = tipo.equals("n");
            System.out.print("Digite o " + (isNacional ? "CPF" : "passaporte") + " do cliente: ");
            String documento = scanner.nextLine();
            
            Cliente cliente = buscarClientePorDocumentoGUI(documento, isNacional);

            if (cliente == null) {
                System.out.println("❌ Cliente não encontrado.");
                return;
            }

            PacoteService pacoteService = new PacoteService(scanner); // Reusa o pacoteService do console
            var todosPacotes = pacoteService.buscarTodosPacotes(); // Já existe no PacoteService
            var pacotesContratados = buscarPacotesContratadosGUI(cliente.getId(), isNacional); // Usa novo método GUI

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

            // Chama o novo método GUI para contratar o pacote
            contratarPacoteParaClienteGUI(cliente.getId(), isNacional, pacoteId);
            System.out.println("✅ Pacote contratado para cliente " + (isNacional ? "nacional" : "estrangeiro") + "!");

        } catch (Exception e) {
            System.err.println("❌ Erro ao contratar pacote: " + e.getMessage());
        }
    }
}
