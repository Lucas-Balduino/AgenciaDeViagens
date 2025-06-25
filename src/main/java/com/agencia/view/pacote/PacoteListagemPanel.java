package com.agencia.view.pacote;

import com.agencia.dao.PacoteDao;
import com.agencia.model.Pacote;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class PacoteListagemPanel extends JPanel {
    private JTextField filtroField;
    private JButton buscarButton;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton editarButton;
    private JButton excluirButton;

    private PacoteDao pacoteDao;
    private PacoteCadastroPanel cadastroPanel;
    private JTabbedPane tabbedPane;

    public PacoteListagemPanel(PacoteCadastroPanel cadastroPanel, JTabbedPane tabbedPane) {
        this.cadastroPanel = cadastroPanel;
        this.tabbedPane = tabbedPane;
        pacoteDao = new PacoteDao();

        setLayout(new BorderLayout(10, 10));

        // Top: filtro
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Buscar por destino:"));
        filtroField = new JTextField(20);
        buscarButton = new JButton("Pesquisar");
        top.add(filtroField);
        top.add(buscarButton);
        add(top, BorderLayout.NORTH);

        // Table
        String[] colunas = {"ID", "Destino", "Duração", "Preço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(tableModel);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Bottom: buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        editarButton = new JButton("Editar");
        excluirButton = new JButton("Excluir");
        bottom.add(editarButton);
        bottom.add(excluirButton);
        add(bottom, BorderLayout.SOUTH);

        // Actions
        buscarButton.addActionListener((ActionEvent e) -> buscarPacotes());
        excluirButton.addActionListener((ActionEvent e) -> excluirPacoteSelecionado());
        editarButton.addActionListener((ActionEvent e) -> editarPacoteSelecionado());

        // Load all
        buscarPacotes();
    }

    public void buscarPacotes() {
        try {
            String filtro = filtroField.getText().trim();
            List<Pacote> lista = pacoteDao.buscarTodos();
            if (!filtro.isEmpty()) {
                lista.removeIf(p -> !p.getDestino().toLowerCase().contains(filtro.toLowerCase()));
            }
            tableModel.setRowCount(0);
            for (Pacote p : lista) {
                tableModel.addRow(new Object[]{
                        p.getId(), p.getDestino(), p.getDuracao(), p.getPreco()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar pacotes: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirPacoteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(linha, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir este pacote?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            pacoteDao.deletar(id);
            buscarPacotes();
            JOptionPane.showMessageDialog(this, "Pacote excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível excluir. Verifique se há contratações associadas.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarPacoteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(linha, 0);
        try {
            Pacote p = pacoteDao.buscarPorId(id);
            if (p != null) {
                cadastroPanel.preencherParaEdicao(p);
                tabbedPane.setSelectedComponent(cadastroPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacote: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
