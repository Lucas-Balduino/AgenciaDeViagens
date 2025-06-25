package com.agencia.view.pacote;

import com.agencia.dao.PacoteDao;
import com.agencia.model.Pacote;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel; 
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
    // Removendo a referência direta a PacoteCadastroPanel e JTabbedPane,
    // pois a edição agora será feita via diálogo.
    // private PacoteCadastroPanel cadastroPanel; 
    // private JTabbedPane tabbedPane;

    // Construtor ajustado para não receber PacoteCadastroPanel e JTabbedPane
    public PacoteListagemPanel() { // Construtor simplificado
        // Se este painel for usado dentro de um JTabbedPane no PacoteManagementFrame,
        // o PacoteManagementFrame precisará ser ajustado para usar este construtor sem argumentos.
        pacoteDao = new PacoteDao();

        setLayout(new BorderLayout(10, 10));

        // Top: filtro
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Buscar por nome ou destino:")); 
        filtroField = new JTextField(20);
        buscarButton = new JButton("Pesquisar");
        top.add(filtroField);
        top.add(buscarButton);
        add(top, BorderLayout.NORTH);

        // Table
        String[] colunas = {"ID", "Nome do Pacote", "Destino", "Duração", "Preço"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setReorderingAllowed(false); 
        
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
        editarButton.addActionListener((ActionEvent e) -> editarPacoteSelecionado()); // Chama o novo fluxo de edição

        buscarPacotes();
    }

    public void buscarPacotes() {
        try {
            String filtro = filtroField.getText().trim();
            List<Pacote> lista = pacoteDao.buscarTodos();
            
            if (!filtro.isEmpty()) {
                lista.removeIf(p -> !(p.getNome().toLowerCase().contains(filtro.toLowerCase()) || 
                                       p.getDestino().toLowerCase().contains(filtro.toLowerCase())));
            }
            tableModel.setRowCount(0);

            if (lista.isEmpty()) {
                tableModel.addRow(new Object[]{"", "Nenhum pacote encontrado.", "", "", ""});
            } else {
                for (Pacote p : lista) {
                    tableModel.addRow(new Object[]{
                            p.getId(), 
                            p.getNome(), 
                            p.getDestino(), 
                            p.getDuracao(), 
                            p.getPreco()
                    });
                }
            }

            // OCULTAR A COLUNA DE ID APÓS A POPULAÇÃO
            TableColumnModel tcm = tabela.getColumnModel();
            if (tcm.getColumnCount() > 0) { 
                tcm.getColumn(0).setMinWidth(0);
                tcm.getColumn(0).setMaxWidth(0);
                tcm.getColumn(0).setWidth(0);
                tcm.getColumn(0).setPreferredWidth(0); 
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar pacotes: " + ex.getMessage(),
                    "Erro de Banco", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); 
        }
    }

    private void excluirPacoteSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pacote.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long id = (Long) tableModel.getValueAt(linha, 0); 
        String nomePacote = (String) tableModel.getValueAt(linha, 1); 

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o pacote '" + nomePacote + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            if (pacoteDao.pacoteEstaContratado(id)) {
                JOptionPane.showMessageDialog(this,
                        "Este pacote está contratado por um ou mais clientes e não pode ser excluído.",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            pacoteDao.deletar(id);
            buscarPacotes();
            JOptionPane.showMessageDialog(this, "Pacote excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir. Detalhes: " + ex.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
                // *** CHAMA O NOVO DIÁLOGO DE EDIÇÃO ***
                EditarPacoteDialog dialog = new EditarPacoteDialog(SwingUtilities.getWindowAncestor(this), p);
                dialog.setVisible(true);

                // Após o diálogo ser fechado, se houve atualização, recarrega a lista
                if (dialog.isUpdated()) {
                    buscarPacotes(); 
                }

            } else {
                 JOptionPane.showMessageDialog(this, "Pacote não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacote para edição: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
