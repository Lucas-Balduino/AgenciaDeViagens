package com.agencia.view;

import com.agencia.dao.ClienteDao;
import com.agencia.model.Cliente;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Nacional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClienteListagemPanel extends JPanel {
    private JTextField filtroField;
    private JButton buscarButton;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton excluirButton;
    private JButton editarButton;

    private ClienteDao clienteDao;
    private ClienteNacionalPanel nacionalPanel;
    private ClienteEstrangeiroPanel estrangeiroPanel;
    private JTabbedPane tabbedPane;

    public ClienteListagemPanel(ClienteNacionalPanel nacionalPanel, ClienteEstrangeiroPanel estrangeiroPanel, JTabbedPane tabbedPane) {
        this.nacionalPanel = nacionalPanel;
        this.estrangeiroPanel = estrangeiroPanel;
        this.tabbedPane = tabbedPane;
        this.clienteDao = new ClienteDao();

        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Buscar por nome ou documento:"));
        filtroField = new JTextField(20);
        buscarButton = new JButton("Pesquisar");
        topPanel.add(filtroField);
        topPanel.add(buscarButton);
        add(topPanel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Tipo", "Documento", "E-mail", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        excluirButton = new JButton("Excluir");
        editarButton = new JButton("Editar");

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(editarButton);
        bottomPanel.add(excluirButton);
        add(bottomPanel, BorderLayout.SOUTH);

        buscarButton.addActionListener(e -> buscarClientes());
        excluirButton.addActionListener(e -> excluirClienteSelecionado());
        editarButton.addActionListener(e -> editarClienteSelecionado());

        buscarClientes();
    }

    public void buscarClientes() {
        try {
            String filtro = filtroField.getText().trim();
            List<Cliente> lista = clienteDao.buscarTodosClientesPorNomeOuDocumento(filtro);
            tableModel.setRowCount(0);
            for (Cliente c : lista) {
                String tipo = (c instanceof Nacional) ? "Nacional" : "Estrangeiro";
                String doc = (c instanceof Nacional) ? ((Nacional) c).getCpf() : ((Estrangeiro) c).getPassaporte();
                tableModel.addRow(new Object[]{
                        c.getId(),
                        c.getNome(),
                        tipo,
                        doc,
                        c.getEmail(),
                        c.getTelefone()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar clientes: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirClienteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = (String) tableModel.getValueAt(linha, 2);
        String doc = (String) tableModel.getValueAt(linha, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este cliente?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String tipoSigla = tipo.equals("Nacional") ? "n" : "e";
                boolean sucesso = clienteDao.removerClientePorDocumento(doc, tipoSigla);
                if (sucesso) {
                    JOptionPane.showMessageDialog(this,
                            "Cliente excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    buscarClientes();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cliente não encontrado ou não pôde ser excluído.",
                            "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir cliente: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarClienteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um cliente para editar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(linha, 0);
        String tipo = (String) tableModel.getValueAt(linha, 2);
        String doc = (String) tableModel.getValueAt(linha, 3);

        try {
            if ("Nacional".equals(tipo)) {
                Nacional nacional = clienteDao.buscarNacionalPorCpf(doc);
                if (nacional != null) {
                    nacionalPanel.preencherParaEdicao(nacional);
                    tabbedPane.setSelectedComponent(nacionalPanel);
                }
            } else if ("Estrangeiro".equals(tipo)) {
                Estrangeiro estrangeiro = clienteDao.buscarEstrangeiroPorPassaporte(doc);
                if (estrangeiro != null) {
                    estrangeiroPanel.preencherParaEdicao(estrangeiro);
                    tabbedPane.setSelectedComponent(estrangeiroPanel);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados do cliente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
