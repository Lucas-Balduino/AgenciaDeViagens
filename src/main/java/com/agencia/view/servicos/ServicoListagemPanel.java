package com.agencia.view.servicos;

import com.agencia.model.Servico;
import com.agencia.service.ServicoService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Scanner; // Necessário porque ServicoService ainda tem construtor com Scanner

public class ServicoListagemPanel extends JPanel {
    private JTextField filtroField;
    private JButton buscarButton;
    private JTable tabela;
    private DefaultTableModel tableModel;
    private JButton excluirButton;

    private ServicoService servicoService;

    public ServicoListagemPanel() {
        // Inicializa o ServicoService
        this.servicoService = new ServicoService(new Scanner(System.in));

        setLayout(new BorderLayout(10, 10)); // Espaçamento entre os componentes

        // Painel superior para pesquisa
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("Pesquisar por Nome:"));
        filtroField = new JTextField(20);
        buscarButton = new JButton("Pesquisar");
        topPanel.add(filtroField);
        topPanel.add(buscarButton);
        add(topPanel, BorderLayout.NORTH);

        // Configuração da tabela
        String[] colunas = {"ID", "Nome", "Duração", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
            // Oculta a coluna ID da visualização, mas mantém no modelo para acesso
            // (Mais avançado: Customizar renderer ou usar TableColumnModel para ocultar)
            // Por enquanto, ela será visível mas podemos instruir para não exibí-la se o design exigir.
        };

        tabela = new JTable(tableModel);
        // Desabilita reordenação de colunas para melhor consistência
        tabela.getTableHeader().setReorderingAllowed(false);
        // Torna a seleção de linha inteira
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        JScrollPane scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior para botões de ação
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        excluirButton = new JButton("Excluir Serviço Selecionado");
        bottomPanel.add(excluirButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Adiciona Listeners
        buscarButton.addActionListener(e -> buscarServicos());
        excluirButton.addActionListener(e -> excluirServicoSelecionado());

        // Carrega os serviços inicialmente
        buscarServicos();
    }

    /**
     * Busca os serviços do banco de dados (filtrando se houver texto no campo de filtro)
     * e popula a JTable.
     */
    public void buscarServicos() {
        tableModel.setRowCount(0); // Limpa as linhas existentes

        try {
            List<Servico> servicos = servicoService.buscarTodosServicosGUI(); // Pega todos os serviços
            String filtroNome = filtroField.getText().trim();

            if (servicos.isEmpty()) {
                tableModel.addRow(new Object[]{"", "Nenhum serviço encontrado.", "", ""});
                return;
            }

            for (Servico s : servicos) {
                // Aplica o filtro de nome (case-insensitive)
                if (filtroNome.isEmpty() || s.getNome().toLowerCase().contains(filtroNome.toLowerCase())) {
                    tableModel.addRow(new Object[]{
                            s.getId(), // ID para uso interno (exclusão)
                            s.getNome(),
                            s.getDuracao(),
                            s.getDescricao()
                    });
                }
            }
            if (tableModel.getRowCount() == 0 && !filtroNome.isEmpty()) {
                 tableModel.addRow(new Object[]{"", "Nenhum serviço corresponde ao filtro.", "", ""});
            }


        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar serviços: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Exclui o serviço selecionado na tabela.
     */
    private void excluirServicoSelecionado() {
        int selectedRow = tabela.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um serviço para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega o ID da coluna oculta/primeira (índice 0)
        Long servicoId = (Long) tableModel.getValueAt(selectedRow, 0);
        String servicoNome = (String) tableModel.getValueAt(selectedRow, 1); // Nome para a confirmação

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente excluir o serviço '" + servicoNome + "'?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                servicoService.removerServico(servicoId); // O ServicoService.removerServico() atual usa Long ID
                JOptionPane.showMessageDialog(this,
                        "Serviço excluído com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                buscarServicos(); // Atualiza a lista após a exclusão
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir serviço: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
