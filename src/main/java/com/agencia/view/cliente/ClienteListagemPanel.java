package com.agencia.view.cliente; // Pacote correto

import com.agencia.dao.ClienteDao;
import com.agencia.model.Cliente;
import com.agencia.model.Estrangeiro;
import com.agencia.model.Nacional;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel; // Import adicionado
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

    // Construtor com os painéis de cadastro e o tabbedPane (se ainda forem necessários para o fluxo de edição de clientes)
    // Se a edição agora for *apenas* via diálogo, estas referências podem não ser estritamente necessárias
    // para este painel, mas as mantemos para compatibilidade com o ClienteManagementFrame.
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

        // A coluna "ID" é a primeira, e será ocultada.
        // O ID é mantido no modelo da tabela para uso interno (edição/exclusão).
        String[] colunas = {"ID", "Nome", "Tipo", "Documento", "E-mail", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(tableModel);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        // Define que a tabela não pode reordenar colunas
        tabela.getTableHeader().setReorderingAllowed(false); 

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
            List<Cliente> lista = clienteDao.buscarTodosClientes(filtro); 
            tableModel.setRowCount(0); 
            
            if (lista.isEmpty()) {
                tableModel.addRow(new Object[]{"", "Nenhum cliente encontrado.", "", "", "", ""});
                return;
            }

            for (Cliente c : lista) {
                String tipo = (c instanceof Nacional) ? "Nacional" : "Estrangeiro";
                String doc = ""; 
                if (c instanceof Nacional) {
                    doc = ((Nacional) c).getCpf();
                } else if (c instanceof Estrangeiro) {
                    doc = ((Estrangeiro) c).getPassaporte();
                }
                
                tableModel.addRow(new Object[]{
                        c.getId(), // ID é adicionado no modelo
                        c.getNome(),
                        tipo,
                        doc,
                        c.getEmail(),
                        c.getTelefone()
                });
            }

            // *** OCULTAR A COLUNA DE ID APÓS A POPULAÇÃO ***
            TableColumnModel tcm = tabela.getColumnModel();
            if (tcm.getColumnCount() > 0) {
                tcm.getColumn(0).setMinWidth(0);
                tcm.getColumn(0).setMaxWidth(0);
                tcm.getColumn(0).setWidth(0);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar clientes: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); 
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

        // Pega o ID da linha selecionada (coluna 0, mesmo que esteja oculta)
        Long idCliente = (Long) tableModel.getValueAt(linha, 0); 
        String nomeCliente = (String) tableModel.getValueAt(linha, 1); // Nome para a confirmação
        String tipoCliente = (String) tableModel.getValueAt(linha, 2);
        String docCliente = (String) tableModel.getValueAt(linha, 3);


        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o cliente '" + nomeCliente + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String tipoSigla = tipoCliente.equals("Nacional") ? "n" : "e";
                boolean sucesso = clienteDao.removerClientePorDocumento(docCliente, tipoSigla); 
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
                ex.printStackTrace(); 
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

        Long idCliente = (Long) tableModel.getValueAt(linha, 0); // Pega o ID da coluna 0 (mesmo que esteja oculta)
        String tipoClienteStr = (String) tableModel.getValueAt(linha, 2);
        String docCliente = (String) tableModel.getValueAt(linha, 3); 
        boolean isNacional = tipoClienteStr.equals("Nacional");

        try {
            // Busca o objeto Cliente completo para passar para o diálogo de edição
            Cliente clienteParaEditar = null;
            if (isNacional) {
                clienteParaEditar = clienteDao.buscarNacionalPorCpf(docCliente);
            } else {
                clienteParaEditar = clienteDao.buscarEstrangeiroPorPassaporte(docCliente);
            }

            if (clienteParaEditar != null) {
                // *** CHAMA O NOVO DIÁLOGO DE EDIÇÃO ***
                EditarClienteDialog dialog = new EditarClienteDialog(SwingUtilities.getWindowAncestor(this), clienteParaEditar);
                dialog.setVisible(true);

                // Após o diálogo ser fechado, se houve atualização, recarrega a lista
                if (dialog.isUpdated()) {
                    buscarClientes(); 
                }

            } else {
                JOptionPane.showMessageDialog(this, "Cliente não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar dados do cliente para edição: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
