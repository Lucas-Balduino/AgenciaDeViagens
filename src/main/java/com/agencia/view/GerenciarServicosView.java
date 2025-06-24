package com.agencia.view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

// Importar as classes das views corretas
import com.agencia.view.servicos.CadastrarServicoView; 
import com.agencia.view.servicos.ListarServicosView; // Novo import

import java.awt.Color;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GerenciarServicosView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GerenciarServicosView frame = new GerenciarServicosView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GerenciarServicosView() {
		setTitle("Agência de Viagens - Gerenciar Serviços");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("GERENCIAR SERVIÇOS");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut);
		
		JButton btnCadastrarNovoServico = new JButton("Cadastrar Novo Serviço");
		btnCadastrarNovoServico.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CadastrarServicoView cadastrarServico = new CadastrarServicoView();
		        cadastrarServico.setVisible(true);
			}
		});
		btnCadastrarNovoServico.setFont(new Font("Verdana", Font.BOLD, 14));
		btnCadastrarNovoServico.setAlignmentX(0.5f);
		panel.add(btnCadastrarNovoServico);
		
		Component verticalStrut_1 = Box.createVerticalStrut(10);
		panel.add(verticalStrut_1);
		
		JButton btnListarServicos = new JButton("Listar Todos os Serviços"); // Renomeado para clareza
		btnListarServicos.addActionListener(new ActionListener() { // Adicionado ActionListener
			public void actionPerformed(ActionEvent e) {
				ListarServicosView listarServicos = new ListarServicosView();
				listarServicos.setVisible(true);
			}
		});
		btnListarServicos.setFont(new Font("Verdana", Font.BOLD, 14));
		btnListarServicos.setAlignmentX(0.5f);
		panel.add(btnListarServicos);
		
		Component verticalStrut_2 = Box.createVerticalStrut(10);
		panel.add(verticalStrut_2);
		
		JButton btnNewButton_2 = new JButton("Buscar Serviço");
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton_2.setAlignmentX(0.5f);
		panel.add(btnNewButton_2);
		
		Component verticalStrut_3 = Box.createVerticalStrut(10);
		panel.add(verticalStrut_3);
		
		JButton btnNewButton_3 = new JButton("Remover Serviço");
		btnNewButton_3.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNewButton_3.setAlignmentX(0.5f);
		panel.add(btnNewButton_3);
		
		Component verticalStrut_4 = Box.createVerticalStrut(10);
		panel.add(verticalStrut_4);
		
		JButton btnVoltar = new JButton("Voltar");
		btnVoltar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnVoltar.setFont(new Font("Verdana", Font.BOLD, 14));
		btnVoltar.setAlignmentX(0.5f);
		panel.add(btnVoltar);

	}

}
