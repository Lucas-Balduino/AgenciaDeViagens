package com.agencia.view.cliente;

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

    // Assumindo que este construtor é o que está sendo usado
    public ClienteListagemPanel(ClienteNacionalPanel nacionalPanel, ClienteEstrangeiroPanel estrangeiroPanel, JTabbedPane tabbedPane) {
        // As referências a nacionalPanel, estrangeiroPanel e tabbedPane são passadas,
        // mas não são usadas diretamente nesta classe para a correção atual.
        // Elas são usadas em métodos como 'editarClienteSelecionado'.
        // this.nacionalPanel = nacionalPanel; // Se estas linhas estiverem descomentadas, mantenha-as
        // this.estrangeiroPanel = estrangeiroPanel; // Se estas linhas estiverem descomentadas, mantenha-as
        // this.tabbedPane = tabbedPane; // Se estas linhas estiverem descomentadas, mantenha-as
        this.clienteDao = new ClienteDao(); // Inicializa o ClienteDao

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

        // Carrega os clientes ao iniciar o painel
        buscarClientes(); 
    }

    public void buscarClientes() {
        try {
            String filtro = filtroField.getText().trim();
            // *** LINHA CORRIGIDA AQUI ***
            List<Cliente> lista = clienteDao.buscarTodosClientes(filtro); // O método foi renomeado no ClienteDao
            tableModel.setRowCount(0); // Limpa as linhas existentes antes de adicionar novas
            
            // Adiciona a lógica para exibir mensagem se a lista estiver vazia
            if (lista.isEmpty()) {
                tableModel.addRow(new Object[]{"", "Nenhum cliente encontrado.", "", "", "", ""});
                return;
            }

            for (Cliente c : lista) {
                String tipo = (c instanceof Nacional) ? "Nacional" : "Estrangeiro";
                String doc = ""; // Inicializa doc
                if (c instanceof Nacional) {
                    doc = ((Nacional) c).getCpf();
                } else if (c instanceof Estrangeiro) {
                    doc = ((Estrangeiro) c).getPassaporte();
                }
                
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
            ex.printStackTrace(); // Imprime o stack trace para depuração
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
                // Este método em ClienteDao.java retorna boolean agora
                boolean sucesso = clienteDao.removerClientePorDocumento(doc, tipoSigla); 
                if (sucesso) {
                    JOptionPane.showMessageDialog(this,
                            "Cliente excluído com sucesso!",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);
                    buscarClientes(); // Atualiza a lista após a exclusão
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
                ex.printStackTrace(); // Imprime o stack trace
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

        // Recupera os dados da linha selecionada
        // Long id = (Long) tableModel.getValueAt(linha, 0); // O ID não é estritamente necessário se buscar por documento
        String tipo = (String) tableModel.getValueAt(linha, 2);
        String doc = (String) tableModel.getValueAt(linha, 3); // Documento (CPF/Passaporte)

        try {
            // Assumindo que você tem acesso aos painéis de cadastro via construtor,
            // ou que ClienteManagementFrame terá métodos para fazer essa transição.
            // O código original ClienteManagementFrame já passa os painéis.
            if (tipo.equals("Nacional")) {
                Nacional nacional = clienteDao.buscarNacionalPorCpf(doc);
                if (nacional != null) {
                    // Chame o método preencherParaEdicao no seu ClienteNacionalPanel
                    // Ex: nacionalPanel.preencherParaEdicao(nacional);
                    // E mude a aba no tabbedPane
                    // Ex: tabbedPane.setSelectedComponent(nacionalPanel);
                    JOptionPane.showMessageDialog(this, "Funcionalidade de edição para cliente Nacional (CPF: " + doc + ") seria carregada.", "Edição Mock", JOptionPane.INFORMATION_MESSAGE);
                } else {
                     JOptionPane.showMessageDialog(this, "Cliente Nacional não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else if (tipo.equals("Estrangeiro")) {
                Estrangeiro estrangeiro = clienteDao.buscarEstrangeiroPorPassaporte(doc);
                if (estrangeiro != null) {
                    // Chame o método preencherParaEdicao no seu ClienteEstrangeiroPanel
                    // Ex: estrangeiroPanel.preencherParaEdicao(estrangeiro);
                    // E mude a aba no tabbedPane
                    // Ex: tabbedPane.setSelectedComponent(estrangeiroPanel);
                    JOptionPane.showMessageDialog(this, "Funcionalidade de edição para cliente Estrangeiro (Passaporte: " + doc + ") seria carregada.", "Edição Mock", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Cliente Estrangeiro não encontrado para edição.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
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
