package com.agencia.view.servicos;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JScrollPane; // Import necessário para a barra de rolagem da tabela
import javax.swing.JTable;      // Import necessário para a tabela
import javax.swing.table.DefaultTableModel; // Import necessário para o modelo da tabela
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector; // Para DefaultTableModel

import com.agencia.model.Servico;
import com.agencia.service.ServicoService;
import java.util.Scanner; // Necessário porque ServicoService ainda tem construtor com Scanner

public class ListarServicosView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table; // A tabela que exibirá os serviços
	private DefaultTableModel tableModel; // O modelo de dados para a tabela

	private ServicoService servicoService; // Instância do serviço

	/**
	 * Método principal para iniciar a aplicação (apenas para teste de UI independente).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ListarServicosView frame = new ListarServicosView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Cria o frame (janela) da Lista de Serviços.
	 */
	public ListarServicosView() {
        // Inicializa o ServicoService
        servicoService = new ServicoService(new Scanner(System.in)); // Passa um Scanner dummy

		setTitle("Agência de Viagens - Listar Serviços");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 700, 500); // Aumentei o tamanho para a tabela
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblTitle = new JLabel("LISTA DE SERVIÇOS CADASTRADOS");
		lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblTitle, BorderLayout.NORTH);
		
		// Define as colunas da tabela
		String[] columnNames = {"Nome", "Duração", "Descrição"};
		tableModel = new DefaultTableModel(columnNames, 0) {
            // Torna as células da tabela não editáveis
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };
		
		table = new JTable(tableModel);
        // Adiciona a tabela a um JScrollPane para que ela tenha barras de rolagem se o conteúdo for grande
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane, BorderLayout.CENTER); // Adiciona o scrollPane ao centro do contentPane
		
		JPanel buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // Fecha a janela atual
			}
		});
		buttonPanel.add(btnVoltar);

        // Carrega os dados na tabela ao inicializar a view
        loadServicesIntoTable();
	}

    /**
     * Carrega a lista de serviços do ServicoService e popula a JTable.
     */
    private void loadServicesIntoTable() {
        // Limpa todas as linhas existentes na tabela antes de adicionar novas
        tableModel.setRowCount(0);

        List<Servico> servicos = servicoService.buscarTodosServicosGUI();

        if (servicos.isEmpty()) {
            // Se não houver serviços, adiciona uma linha indicando isso
            tableModel.addRow(new Object[]{"Nenhum serviço encontrado.", "", ""});
            return;
        }

        for (Servico servico : servicos) {
            // Adiciona uma nova linha com os dados do serviço
            tableModel.addRow(new Object[]{servico.getNome(), servico.getDuracao(), servico.getDescricao()});
        }
    }
}
