package com.agencia.view.contratacao;

import com.agencia.model.Cliente;
import com.agencia.model.Nacional;
import com.agencia.model.Estrangeiro;
import com.agencia.service.ClienteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner; // Necessário para o construtor de Services

public class ContratarPacoteClientePanel extends JPanel {

    private JTextField filtroClienteField;
    private JButton buscarClienteButton;
    private JTable clientesTable;
    private DefaultTableModel clientesTableModel;
    private JButton gerenciarPacotesClienteButton;

    private ClienteService clienteService;

    public ContratarPacoteClientePanel() {
        this.clienteService = new ClienteService(new Scanner(System.in)); // Instancia com Scanner

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Gerenciar Pacotes por Cliente"));

        // Painel superior para pesquisa de cliente
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.add(new JLabel("Pesquisar Cliente (Nome/Doc):"));
        filtroClienteField = new JTextField(25);
        buscarClienteButton = new JButton("Pesquisar");
        topPanel.add(filtroClienteField);
        topPanel.add(buscarClienteButton);
        add(topPanel, BorderLayout.NORTH);

        // Tabela de clientes
        String[] colunasClientes = {"ID", "Nome", "Tipo", "Documento", "E-mail", "Telefone"};
        clientesTableModel = new DefaultTableModel(colunasClientes, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Células não editáveis
            }
        };
        clientesTable = new JTable(clientesTableModel);
        clientesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas uma seleção
        JScrollPane scrollPane = new JScrollPane(clientesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com o botão de gerenciar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        gerenciarPacotesClienteButton = new JButton("Gerenciar Pacotes do Cliente Selecionado");
        bottomPanel.add(gerenciarPacotesClienteButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Adiciona Action Listeners
        buscarClienteButton.addActionListener(e -> loadClientesIntoTable());
        gerenciarPacotesClienteButton.addActionListener(e -> gerenciarPacotesDoCliente());

        // Carrega os clientes na tabela ao iniciar o painel
        loadClientesIntoTable();
    }

    /**
     * Carrega os clientes na tabela, aplicando filtro se houver.
     */
    public void loadClientesIntoTable() {
        clientesTableModel.setRowCount(0); // Limpa a tabela

        try {
            String filtro = filtroClienteField.getText().trim();
            List<Cliente> clientes = clienteService.buscarTodosClientesGUI(filtro); // Chama o ClienteService atualizado

            if (clientes.isEmpty()) {
                clientesTableModel.addRow(new Object[]{"", "Nenhum cliente encontrado.", "", "", "", ""});
                return;
            }

            for (Cliente c : clientes) {
                String tipo = (c instanceof Nacional) ? "Nacional" : "Estrangeiro";
                // A forma de obter o documento depende do tipo de cliente
                String documento = "";
                if (c instanceof Nacional) {
                    documento = ((Nacional) c).getCpf();
                } else if (c instanceof Estrangeiro) {
                    documento = ((Estrangeiro) c).getPassaporte();
                }
                
                clientesTableModel.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    tipo,
                    documento,
                    c.getEmail(),
                    c.getTelefone()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar clientes: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Abre o diálogo para gerenciar pacotes do cliente selecionado.
     */
    private void gerenciarPacotesDoCliente() {
        int selectedRow = clientesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente na tabela para gerenciar seus pacotes.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long clienteId = (Long) clientesTableModel.getValueAt(selectedRow, 0);
        String tipoClienteStr = (String) clientesTableModel.getValueAt(selectedRow, 2);
        String documentoCliente = (String) clientesTableModel.getValueAt(selectedRow, 3);
        boolean isNacional = tipoClienteStr.equals("Nacional");

        try {
            // Re-busca o cliente completo para passar para o diálogo
            Cliente clienteSelecionado = clienteService.buscarClientePorDocumentoGUI(documentoCliente, isNacional);
            if (clienteSelecionado != null) {
                // Abre o novo diálogo de gerenciamento de pacotes do cliente
                GerenciarPacotesClienteDialog dialog = new GerenciarPacotesClienteDialog(this, clienteSelecionado);
                dialog.setVisible(true);
                // Opcional: Recarregar a lista de clientes após o diálogo fechar
                loadClientesIntoTable(); 
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro: Cliente não encontrado. Por favor, tente novamente.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar detalhes do cliente: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
