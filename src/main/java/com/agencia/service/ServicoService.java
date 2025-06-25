package com.agencia.service;

import com.agencia.dao.ServicoDao;
import com.agencia.model.Servico;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ServicoService {
    private ServicoDao servicoDao = new ServicoDao();
    private Scanner scanner;

    public ServicoService(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Cadastra um novo serviço na base de dados.
     * Este método é adaptado para ser chamado por uma interface gráfica,
     * recebendo os dados diretamente como parâmetros e lançando exceções
     * em caso de erro, em vez de interagir com o console.
     *
     * @param nome O nome do serviço.
     * @param duracao A duração do serviço em dias/horas.
     * @param descricao A descrição do serviço.
     * @throws IllegalArgumentException Se algum dado de entrada for inválido.
     * @throws Exception Se ocorrer um erro durante a inserção no banco de dados.
     */
    public void cadastrarServico(String nome, int duracao, String descricao) throws Exception {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do serviço não pode ser vazio.");
        }
        if (duracao <= 0) {
            throw new IllegalArgumentException("A duração do serviço deve ser um número inteiro maior que zero.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do serviço não pode ser vazia.");
        }

        try {
            Servico servico = new Servico(nome, duracao, descricao);
            servicoDao.inserir(servico);
        } catch (Exception e) {
            throw new Exception("Falha ao cadastrar serviço no banco de dados: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna uma lista de todos os serviços.
     * Este método é ideal para uso em interfaces gráficas, pois retorna os dados
     * diretamente em vez de imprimi-los no console.
     *
     * @return Uma lista de objetos Servico, ou uma lista vazia se nenhum serviço for encontrado ou ocorrer um erro.
     */
    public List<Servico> buscarTodosServicosGUI() {
        try {
            return servicoDao.buscarTodos();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviços para GUI: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace para depuração
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }

    /**
     * Remove um serviço da base de dados pelo seu ID.
     * Este método é adaptado para ser chamado por uma interface gráfica.
     *
     * @param id O ID do serviço a ser removido.
     * @throws Exception Se ocorrer um erro durante a remoção no banco de dados
     * ou se o serviço não for encontrado (se o DAO lançar).
     */
    public void removerServico(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID do serviço inválido para remoção.");
        }
        try {
            // O DAO pode lançar exceção se o serviço não existir ou se houver erro na exclusão.
            // Para este caso, vamos assumir que o DAO deleta e lança exceção em caso de falha.
            servicoDao.deletar(id);
        } catch (Exception e) {
            throw new Exception("Falha ao remover serviço do banco de dados: " + e.getMessage(), e);
        }
    }

    // --- Métodos originais que ainda utilizam Scanner (mantidos para compatibilidade) ---
    public void cadastrarServico() {
        System.out.println("\n=== CADASTRAR SERVIÇO ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Duração (horas): ");
        int duracao = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();

        try {
            cadastrarServico(nome, duracao, descricao);
            System.out.println("✅ Serviço cadastrado!");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erro de validação: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar serviço: " + e.getMessage());
        }
    }

    // Método original removerServico() que usa Scanner (mantido por compatibilidade com Main.java)
    // No entanto, para as GUIs, o método removerServico(Long id) deve ser usado.
    public void removerServico() {
        System.out.print("Digite o ID do serviço para remover: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // limpar buffer

        try {
            // Chama o método removerServico(Long id) agora que ele existe
            removerServico(id);
            System.out.println("✅ Serviço removido com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover serviço: " + e.getMessage());
        }
    }

    public Servico buscarServicoPorId(Long id) {
        try {
            Servico servico = servicoDao.buscarPorId(id);
            if (servico == null) {
                System.out.println("❌ Serviço não encontrado.");
            } else {
                System.out.println("\n🛠️ Serviço [" + servico.getId() + "]");
                System.out.println("Nome: " + servico.getNome());
                System.out.println("Duração: " + servico.getDuracao() + " dias");
                System.out.println("Descrição: " + servico.getDescricao());
            }
            return servico;
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviço: " + e.getMessage());
            return null;
        }
    }

    public void buscarServicoPorId() {
        System.out.print("Digite o ID do serviço: ");
        Long id = scanner.nextLong();
        scanner.nextLine();
        buscarServicoPorId(id);
    }
}
