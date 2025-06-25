package com.agencia.view.contratacao;

import com.agencia.model.Pacote;
import com.agencia.service.PacoteService;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner; // Necessário para o construtor de Service

public class ContratarServicoPacotePanel extends JPanel {

    private JTable pacotesTable;
    private DefaultTableModel pacotesTableModel;
    private JButton gerenciarServicosButton;

    private PacoteService pacoteService;

    public ContratarServicoPacotePanel() {
        this.pacoteService = new PacoteService(new Scanner(System.in)); // Instancia com Scanner

        setLayout(new BorderLayout(10, 10)); // Layout principal do painel
        setBorder(BorderFactory.createTitledBorder("Gerenciar Serviços por Pacote"));

        // Painel superior com título e tabela de pacotes
        JLabel titleLabel = new JLabel("Selecione um Pacote para Gerenciar Seus Serviços");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        String[] colunasPacotes = {"ID", "Nome do Pacote", "Destino", "Duração (dias)", "Preço"};
        pacotesTableModel = new DefaultTableModel(colunasPacotes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Células não editáveis
            }
        };
        pacotesTable = new JTable(pacotesTableModel);
        pacotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        JScrollPane scrollPane = new JScrollPane(pacotesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com botão de gerenciar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        gerenciarServicosButton = new JButton("Gerenciar Serviços do Pacote Selecionado");
        bottomPanel.add(gerenciarServicosButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Adiciona Action Listener
        gerenciarServicosButton.addActionListener(e -> gerenciarServicosDoPacote());

        // Carrega os pacotes na tabela ao inicializar o painel
        loadPackagesIntoTable();
    }

    /**
     * Carrega todos os pacotes na tabela.
     */
    public void loadPackagesIntoTable() {
        pacotesTableModel.setRowCount(0); // Limpa a tabela

        try {
            List<Pacote> todosPacotes = pacoteService.buscarTodosPacotesGUI();

            if (todosPacotes.isEmpty()) {
                pacotesTableModel.addRow(new Object[]{"", "Nenhum pacote cadastrado.", "", "", ""});
                return;
            }

            for (Pacote p : todosPacotes) {
                pacotesTableModel.addRow(new Object[]{
                    p.getId(),
                    p.getNome(),
                    p.getDestino(),
                    p.getDuracao(),
                    String.format("%.2f", p.getPreco()) // Formata o preço
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacotes: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Abre uma nova janela/dialog para gerenciar os serviços do pacote selecionado.
     */
    private void gerenciarServicosDoPacote() {
        int selectedRow = pacotesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um pacote na tabela para gerenciar seus serviços.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long pacoteId = (Long) pacotesTableModel.getValueAt(selectedRow, 0);
        Pacote pacoteSelecionado = pacoteService.buscarPacotePorIdGUI(pacoteId);

        if (pacoteSelecionado != null) {
            // Passa 'this' (o ContratarServicoPacotePanel) como parente para centralizar o diálogo
            GerenciarServicosDoPacoteDialog dialog = new GerenciarServicosDoPacoteDialog(this, pacoteSelecionado);
            dialog.setVisible(true);
            loadPackagesIntoTable(); // Atualiza a lista de pacotes no painel após fechar o diálogo de gerenciamento
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro: Pacote não encontrado. Por favor, tente novamente.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
