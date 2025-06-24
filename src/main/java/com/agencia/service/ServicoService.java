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

    public void cadastrarServico(String nome, int duracao, String descricao) throws Exception {
        // Validações básicas (replicando a lógica que estava implícita na versão de console)
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
            // Nenhuma mensagem de sucesso aqui; a UI que chamou fará isso.
        } catch (Exception e) {
            // Re-lança a exceção para que a camada de UI possa tratá-la e exibir a mensagem adequada.
            throw new Exception("Falha ao cadastrar serviço no banco de dados: " + e.getMessage(), e);
        }
    }
    
    public List<Servico> buscarTodosServicosGUI() {
        try {
            return servicoDao.buscarTodos();
        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviços para GUI: " + e.getMessage());
            e.printStackTrace(); // Imprime o stack trace para depuração
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }
    
    public void listarTodosServicos() {
        try {
            var servicos = servicoDao.buscarTodos();

            if (servicos.isEmpty()) {
                System.out.println("📭 Nenhum serviço encontrado.");
                return;
            }

            System.out.println("\n=== LISTA DE SERVIÇOS ===");
            for (Servico servico : servicos) {
                System.out.println("[" + servico.getId() + "] " + servico.getNome() +
                        " (Duração de " + servico.getDuracao() + " dias)");
            }

        } catch (Exception e) {
            System.err.println("❌ Erro ao listar serviços: " + e.getMessage());
        }
    }
    
    public void buscarServicoPorId() {
        System.out.print("Digite o ID do serviço: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // limpar buffer

        try {
            Servico servico = servicoDao.buscarPorId(id);

            if (servico == null) {
                System.out.println("❌ Serviço não encontrado.");
                return;
            }

            System.out.println("\n🛠️ Serviço [" + servico.getId() + "]");
            System.out.println("Nome: " + servico.getNome());
            System.out.println("Duração: " + servico.getDuracao() + " dias");
            System.out.println("Descrição: " + servico.getDescricao());

        } catch (Exception e) {
            System.err.println("❌ Erro ao buscar serviço: " + e.getMessage());
        }
    }

    public void removerServico() {
        System.out.print("Digite o ID do serviço para remover: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // limpar buffer

        try {
            Servico servico = servicoDao.buscarPorId(id);

            if (servico == null) {
                System.out.println("❌ Serviço não encontrado.");
                return;
            }

            servicoDao.deletar(id);
            System.out.println("✅ Serviço removido com sucesso!");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao remover serviço: " + e.getMessage());
        }
    }


}