package com.agencia.view.servicos; // Pacote correto

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JOptionPane; // Import para as mensagens de feedback

import com.agencia.service.ServicoService;
import java.util.Scanner; // Import necessário porque ServicoService ainda tem construtor com Scanner

public class CadastrarServicoView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtNome;
	private JTextField txtDuracao;   // Corrigido para txtDuracao
	private JTextField txtDescricao; // Corrigido para txtDescricao

    // Instância do serviço para interagir com a lógica de negócio
    private ServicoService servicoService; 

	/**
	 * Método principal para iniciar a aplicação (apenas para teste de UI independente).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CadastrarServicoView frame = new CadastrarServicoView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Cria o frame (janela) do Cadastro de Serviços.
	 */
	public CadastrarServicoView() {
        // MUITO IMPORTANTE: Inicializa o ServicoService AQUI
        // Passamos um Scanner dummy, pois o método cadastrarServico(String, int, String) não o usará.
        // Em uma aplicação GUI mais robusta, essa instância poderia ser passada via injeção de dependência
        // para evitar acoplamento direto ou a necessidade de um Scanner.
        servicoService = new ServicoService(new Scanner(System.in));

		setTitle("Cadastro de Serviços");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblTitle = new JLabel("Preencha os dados para cadastro de serviço");
		lblTitle.setFont(new Font("Verdana", Font.BOLD, 14));
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER); // Centraliza o título
		contentPane.add(lblTitle, BorderLayout.NORTH);
		
		JPanel inputPanel = new JPanel(); // Painel para os campos de entrada
		contentPane.add(inputPanel, BorderLayout.CENTER);
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		// Espaçamento vertical
		Component verticalStrut = Box.createVerticalStrut(20);
		inputPanel.add(verticalStrut);
		
		// Campo para Nome do Serviço
		txtNome = new JTextField();
		txtNome.setText("Nome do Serviço"); // Texto de placeholder
		txtNome.setToolTipText("Digite o nome do serviço");
		txtNome.setFont(new Font("Verdana", Font.PLAIN, 12));
        txtNome.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o campo
		inputPanel.add(txtNome);
		txtNome.setColumns(10);
		
		// Espaçamento vertical
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		inputPanel.add(verticalStrut_1);
		
		// Campo para Duração (antes era Horas, agora mais genérico "Duracao")
		txtDuracao = new JTextField(); 
		txtDuracao.setFont(new Font("Verdana", Font.PLAIN, 12));
		txtDuracao.setText("Duração (dias/horas)"); // Texto de placeholder
		txtDuracao.setToolTipText("Digite a duração do serviço em dias ou horas (somente números)");
        txtDuracao.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o campo
		inputPanel.add(txtDuracao);
		txtDuracao.setColumns(10);
		
		// Espaçamento vertical
		Component verticalStrut_2 = Box.createVerticalStrut(10);
		inputPanel.add(verticalStrut_2);
		
		// Campo para Descrição
		txtDescricao = new JTextField(); 
		txtDescricao.setFont(new Font("Verdana", Font.PLAIN, 12));
		txtDescricao.setText("Descrição do Serviço"); // Texto de placeholder
		txtDescricao.setToolTipText("Digite uma breve descrição do serviço");
        txtDescricao.setAlignmentX(Component.CENTER_ALIGNMENT); // Centraliza o campo
		inputPanel.add(txtDescricao);
		txtDescricao.setColumns(10);
		
		// Espaçamento vertical
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		inputPanel.add(verticalStrut_3);
		
		JPanel buttonPanel = new JPanel(); // Painel para os botões
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Botão Cancelar
		JButton btnCancelar = new JButton("Cancelar");
		btnCancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // Fecha a janela atual
			}
		});
		btnCancelar.setForeground(new Color(0, 0, 0));
		buttonPanel.add(btnCancelar);
		
		// Botão Cadastrar
		JButton btnCadastrar = new JButton("Cadastrar");
		btnCadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 1. Obter os dados dos campos de texto
				String nome = txtNome.getText();
				String descricao = txtDescricao.getText();
				int duracao = 0; // Valor padrão
		
				// 2. Validação e conversão da duração (garantir que é um número válido)
				try {
					duracao = Integer.parseInt(txtDuracao.getText());
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(CadastrarServicoView.this,
							"Por favor, insira um número válido para a duração (ex: 7 para 7 dias).",
							"Erro de Entrada",
							JOptionPane.ERROR_MESSAGE);
					return; // Interrompe a execução se a duração não for um número
				}
		
				// 3. Chamar o ServicoService para cadastrar o serviço
				try {
					servicoService.cadastrarServico(nome, duracao, descricao);
					
					// 4. Feedback de sucesso ao usuário
					JOptionPane.showMessageDialog(CadastrarServicoView.this,
							"Serviço cadastrado com sucesso!",
							"Sucesso",
							JOptionPane.INFORMATION_MESSAGE);
					
					// 5. Fechar a janela de cadastro após o sucesso
					dispose(); 
				} catch (IllegalArgumentException ex) {
					// Captura erros de validação da camada de serviço
					JOptionPane.showMessageDialog(CadastrarServicoView.this,
							"Erro de Validação: " + ex.getMessage(),
							"Erro",
							JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					// Captura outros erros (ex: de banco de dados)
					JOptionPane.showMessageDialog(CadastrarServicoView.this,
							"Erro ao cadastrar serviço: " + ex.getMessage(),
							"Erro",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace(); // Imprime o stack trace para depuração no console
				}
			}
		});
		btnCadastrar.setForeground(new Color(0, 128, 0));
		buttonPanel.add(btnCadastrar);
	}
}
