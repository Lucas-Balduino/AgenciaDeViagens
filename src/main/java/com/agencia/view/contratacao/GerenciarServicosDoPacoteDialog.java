package com.agencia.view.contratacao;

import com.agencia.model.Pacote;
import com.agencia.model.Servico;
import com.agencia.service.PacoteService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner; // Necessário para o construtor de Service

public class GerenciarServicosDoPacoteDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Pacote pacote;
    private PacoteService pacoteService;

    private JTable servicosNoPacoteTable;
    private DefaultTableModel servicosNoPacoteTableModel;
    private JTable servicosDisponiveisTable;
    private DefaultTableModel servicosDisponiveisTableModel;

    private JButton adicionarServicoButton;
    private JButton removerServicoButton;

    /**
     * Construtor para o diálogo de gerenciamento de serviços de um pacote.
     * @param parent O componente pai (geralmente o JFrame ou JPanel que abriu este diálogo).
     * @param pacote O objeto Pacote cujos serviços serão gerenciados.
     */
    public GerenciarServicosDoPacoteDialog(Component parent, Pacote pacote) {
        super(SwingUtilities.getWindowAncestor(parent), "Gerenciar Serviços para: " + pacote.getNome(), ModalityType.APPLICATION_MODAL);
        this.pacote = pacote;
        this.pacoteService = new PacoteService(new Scanner(System.in)); // Instancia com Scanner

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(800, 600); // Tamanho do diálogo
        setLocationRelativeTo(SwingUtilities.getWindowAncestor(parent)); // Centraliza em relação ao pai

        setLayout(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("Gerenciar Serviços do Pacote: " + pacote.getNome() + " (" + pacote.getDestino() + ")");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Painel para as duas tabelas e botões de ação
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 15, 0)); // 1 linha, 2 colunas, com espaçamento
        
        // Tabela de Serviços NO Pacote
        String[] colunasServicos = {"ID", "Nome do Serviço", "Duração (dias/horas)", "Descrição"};
        servicosNoPacoteTableModel = new DefaultTableModel(colunasServicos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        servicosNoPacoteTable = new JTable(servicosNoPacoteTableModel);
        servicosNoPacoteTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablesPanel.add(new JScrollPane(servicosNoPacoteTable));

        // Tabela de Serviços DISPONÍVEIS (não no pacote)
        servicosDisponiveisTableModel = new DefaultTableModel(colunasServicos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        servicosDisponiveisTable = new JTable(servicosDisponiveisTableModel);
        servicosDisponiveisTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablesPanel.add(new JScrollPane(servicosDisponiveisTable));

        add(tablesPanel, BorderLayout.CENTER);

        // Painel para os botões de ação (Adicionar/Remover)
        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        adicionarServicoButton = new JButton("<- Adicionar Serviço");
        removerServicoButton = new JButton("Remover Serviço ->");
        actionButtonPanel.add(adicionarServicoButton);
        actionButtonPanel.add(removerServicoButton);
        add(actionButtonPanel, BorderLayout.SOUTH);

        // Adiciona Action Listeners
        adicionarServicoButton.addActionListener(e -> adicionarServicoAoPacote());
        removerServicoButton.addActionListener(e -> removerServicoDoPacote());

        // Carrega os serviços nas tabelas ao abrir o diálogo
        loadServices();
    }

    /**
     * Carrega e atualiza as duas tabelas de serviços.
     */
    private void loadServices() {
        servicosNoPacoteTableModel.setRowCount(0); // Limpa
        servicosDisponiveisTableModel.setRowCount(0); // Limpa

        try {
            List<Servico> servicosDoPacote = pacoteService.buscarServicosDoPacoteGUI(pacote.getId());
            List<Servico> servicosNaoAssociados = pacoteService.buscarServicosNaoAssociadosAoPacoteGUI(pacote.getId());

            if (servicosDoPacote.isEmpty()) {
                servicosNoPacoteTableModel.addRow(new Object[]{"", "Nenhum serviço neste pacote.", "", ""});
            } else {
                for (Servico s : servicosDoPacote) {
                    servicosNoPacoteTableModel.addRow(new Object[]{s.getId(), s.getNome(), s.getDuracao(), s.getDescricao()});
                }
            }

            if (servicosNaoAssociados.isEmpty()) {
                servicosDisponiveisTableModel.addRow(new Object[]{"", "Nenhum serviço disponível para adicionar.", "", ""});
            } else {
                for (Servico s : servicosNaoAssociados) {
                    servicosDisponiveisTableModel.addRow(new Object[]{s.getId(), s.getNome(), s.getDuracao(), s.getDescricao()});
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar serviços: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Adiciona o serviço selecionado da tabela de disponíveis para a tabela do pacote.
     */
    private void adicionarServicoAoPacote() {
        int selectedRow = servicosDisponiveisTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um serviço da lista de disponíveis para adicionar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long servicoId = (Long) servicosDisponiveisTableModel.getValueAt(selectedRow, 0);
        String servicoNome = (String) servicosDisponiveisTableModel.getValueAt(selectedRow, 1);

        try {
            pacoteService.adicionarServicoAoPacoteGUI(pacote.getId(), servicoId);
            JOptionPane.showMessageDialog(this,
                    "Serviço '" + servicoNome + "' adicionado ao pacote com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadServices(); // Atualiza ambas as tabelas
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao adicionar serviço ao pacote: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Remove o serviço selecionado da tabela do pacote.
     */
    private void removerServicoDoPacote() {
        int selectedRow = servicosNoPacoteTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um serviço na lista do pacote para remover.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long servicoId = (Long) servicosNoPacoteTableModel.getValueAt(selectedRow, 0);
        String servicoNome = (String) servicosNoPacoteTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover o serviço '" + servicoNome + "' dod pacote?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                pacoteService.removerServicoDoPacoteGUI(pacote.getId(), servicoId);
                JOptionPane.showMessageDialog(this,
                        "Serviço '" + servicoNome + "' removido do pacote com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                loadServices(); // Atualiza ambas as tabelas
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao remover serviço do pacote: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
