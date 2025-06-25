package com.agencia.view.contratacao;

import com.agencia.model.Cliente;
import com.agencia.model.Nacional;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Pacote;
import com.agencia.service.ClienteService;
import com.agencia.service.PacoteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Necessário para o construtor de Services
import java.util.stream.Collectors; // Para facilitar filtragem

public class GerenciarPacotesClienteDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Cliente cliente; // Cliente que está sendo gerenciado
    private ClienteService clienteService;
    private PacoteService pacoteService;

    private JTable pacotesContratadosTable;
    private DefaultTableModel pacotesContratadosTableModel;
    private JTable pacotesDisponiveisTable;
    private DefaultTableModel pacotesDisponiveisTableModel;

    private JButton contratarPacoteButton;
    private JButton removerPacoteButton;

    /**
     * Construtor para o diálogo de gerenciamento de pacotes de um cliente.
     * @param parent O componente pai (geralmente o JFrame ou JPanel que abriu este diálogo).
     * @param cliente O objeto Cliente cujos pacotes serão gerenciados.
     */
    public GerenciarPacotesClienteDialog(Component parent, Cliente cliente) {
        // Configura o diálogo como modal, ou seja, bloqueia a interação com a tela pai
        super(SwingUtilities.getWindowAncestor(parent), "Gerenciar Pacotes para: " + cliente.getNome(), ModalityType.APPLICATION_MODAL);
        this.cliente = cliente;
        // Instancia serviços, passando um Scanner dummy para construtores que ainda o exigem
        this.clienteService = new ClienteService(new Scanner(System.in)); 
        this.pacoteService = new PacoteService(new Scanner(System.in)); 

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1000, 700); // Tamanho do diálogo
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(parent)); // Centraliza em relação ao pai

        setLayout(new BorderLayout(10, 10)); // Layout principal do diálogo
        
        // Título do Diálogo
        String tipoCliente = (cliente instanceof Nacional) ? "Nacional" : "Estrangeiro";
        String documento = "";
        if (cliente instanceof Nacional) {
            documento = ((Nacional) cliente).getCpf();
        } else if (cliente instanceof Estrangeiro) {
            documento = ((Estrangeiro) cliente).getPassaporte();
        }
        JLabel titleLabel = new JLabel("Gerenciar Pacotes para: " + cliente.getNome() + " (" + tipoCliente + " - " + documento + ")");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Painel para as duas tabelas
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // 1 linha, 2 colunas, com espaçamento
        
        // Configuração das colunas das tabelas de pacotes
        String[] colunasPacotes = {"ID", "Nome do Pacote", "Destino", "Duração", "Preço"};

        // Tabela de Pacotes CONTRATADOS
        pacotesContratadosTableModel = new DefaultTableModel(colunasPacotes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pacotesContratadosTable = new JTable(pacotesContratadosTableModel);
        pacotesContratadosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel contractedPanel = new JPanel(new BorderLayout());
        contractedPanel.setBorder(BorderFactory.createTitledBorder("Pacotes Contratados"));
        contractedPanel.add(new JScrollPane(pacotesContratadosTable), BorderLayout.CENTER);
        tablesPanel.add(contractedPanel);

        // Tabela de Pacotes DISPONÍVEIS
        pacotesDisponiveisTableModel = new DefaultTableModel(colunasPacotes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pacotesDisponiveisTable = new JTable(pacotesDisponiveisTableModel);
        pacotesDisponiveisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Pacotes Disponíveis para Contratar"));
        availablePanel.add(new JScrollPane(pacotesDisponiveisTable), BorderLayout.CENTER);
        tablesPanel.add(availablePanel);

        add(tablesPanel, BorderLayout.CENTER);

        // Painel para os botões de ação (Contratar/Remover)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        contratarPacoteButton = new JButton("<- Contratar Pacote");
        removerPacoteButton = new JButton("Remover Pacote ->");
        actionButtonPanel.add(contratarPacoteButton);
        actionButtonPanel.add(removerPacoteButton);
        add(actionButtonPanel, BorderLayout.SOUTH);

        // Adiciona Action Listeners
        contratarPacoteButton.addActionListener(e -> contratarPacote());
        removerPacoteButton.addActionListener(e -> removerPacote());

        // Carrega os pacotes nas tabelas ao abrir o diálogo
        loadPacotes();
    }

    /**
     * Carrega e atualiza as duas tabelas de pacotes para o cliente.
     */
    private void loadPacotes() {
        pacotesContratadosTableModel.setRowCount(0); // Limpa
        pacotesDisponiveisTableModel.setRowCount(0); // Limpa

        try {
            boolean isNacional = cliente instanceof Nacional;
            List<Pacote> pacotesContratados = clienteService.buscarPacotesContratadosGUI(cliente.getId(), isNacional);
            List<Pacote> todosPacotes = pacoteService.buscarTodosPacotesGUI();

            // Filtra os pacotes que o cliente AINDA NÃO contratou
            List<Pacote> pacotesNaoContratados = todosPacotes.stream()
                    .filter(p -> pacotesContratados.stream().noneMatch(pc -> pc.getId().equals(p.getId())))
                    .collect(Collectors.toList());

            // Popula a tabela de Pacotes Contratados
            if (pacotesContratados.isEmpty()) {
                pacotesContratadosTableModel.addRow(new Object[]{"", "Nenhum pacote contratado.", "", "", ""});
            } else {
                for (Pacote p : pacotesContratados) {
                    pacotesContratadosTableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getDestino(), p.getDuracao(), String.format("%.2f", p.getPreco())});
                }
            }

            // Popula a tabela de Pacotes Disponíveis
            if (pacotesNaoContratados.isEmpty()) {
                pacotesDisponiveisTableModel.addRow(new Object[]{"", "Nenhum pacote disponível para contratar.", "", "", ""});
            } else {
                for (Pacote p : pacotesNaoContratados) {
                    pacotesDisponiveisTableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getDestino(), p.getDuracao(), String.format("%.2f", p.getPreco())});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacotes para o cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Contrata o pacote selecionado da tabela de disponíveis para o cliente.
     */
    private void contratarPacote() {
        int selectedRow = pacotesDisponiveisTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pacote da lista de disponíveis para contratar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pacoteId = (Long) pacotesDisponiveisTableModel.getValueAt(selectedRow, 0);
        String pacoteNome = (String) pacotesDisponiveisTableModel.getValueAt(selectedRow, 1);
        boolean isNacional = cliente instanceof Nacional;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente contratar o pacote '" + pacoteNome + "' para " + cliente.getNome() + "?",
                "Confirmar Contratação", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteService.contratarPacoteParaClienteGUI(cliente.getId(), isNacional, pacoteId);
                JOptionPane.showMessageDialog(this,
                        "Pacote '" + pacoteNome + "' contratado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadPacotes(); // Atualiza ambas as tabelas
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao contratar pacote: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Remove o pacote selecionado da tabela de contratados do cliente.
     */
    private void removerPacote() {
        int selectedRow = pacotesContratadosTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pacote da lista de contratados para remover.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pacoteId = (Long) pacotesContratadosTableModel.getValueAt(selectedRow, 0);
        String pacoteNome = (String) pacotesContratadosTableModel.getValueAt(selectedRow, 1);
        boolean isNacional = cliente instanceof Nacional;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover o pacote '" + pacoteNome + "' de " + cliente.getNome() + "?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                clienteService.removerPacoteContratadoGUI(cliente.getId(), isNacional, pacoteId); // Chama o método de serviço para remover
                JOptionPane.showMessageDialog(this,
                        "Pacote '" + pacoteNome + "' removido com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadPacotes(); // Atualiza ambas as tabelas
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao remover pacote: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
