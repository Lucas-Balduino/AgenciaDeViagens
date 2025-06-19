package com.agencia.service;

import com.agencia.dao.ServicoDao;
import com.agencia.model.Servico;
import java.util.Scanner;

public class ServicoService {
    private ServicoDao servicoDao = new ServicoDao();
    private Scanner scanner;

    public ServicoService(Scanner scanner) {
        this.scanner = scanner;
    }

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
            Servico servico = new Servico(nome, duracao, descricao);
            servicoDao.inserir(servico);
            System.out.println("✅ Serviço cadastrado!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao cadastrar: " + e.getMessage());
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