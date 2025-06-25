package com.agencia.view;

// Importa ClienteManagementFrame (já existente)
import com.agencia.view.ClienteManagementFrame;
// Importa ServicoManagementFrame (necessário para a nova ligação)
import com.agencia.view.ServicoManagementFrame; 

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout; // Mantido, se o WindowBuilder inseriu
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MenuPrincipalView extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MenuPrincipalView frame = new MenuPrincipalView();
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
	public MenuPrincipalView() {
		setTitle("Agência de Viagens - Menu Principal");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 428, 317);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Bem-vindo à Agência de Viagens!");
		lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel);
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		panel.add(verticalStrut_1);
		
		JButton btnGerenciarCli = new JButton("Gerenciar Clientes");
		btnGerenciarCli.addActionListener(e -> {
			ClienteManagementFrame frame = new ClienteManagementFrame();
			frame.setVisible(true);
		});

		btnGerenciarCli.setAlignmentY(Component.TOP_ALIGNMENT);
		btnGerenciarCli.setFont(new Font("Verdana", Font.BOLD, 14));
		panel.add(btnGerenciarCli);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut);
		
		JButton btnGerenciarPac = new JButton("Gerenciar Pacotes");
		btnGerenciarPac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Botão Gerenciar Pacotes clicado!");
				// Lógica para abrir o PacoteManagementFrame virá aqui
			}
		});
		btnGerenciarPac.setFont(new Font("Verdana", Font.BOLD, 14));
		panel.add(btnGerenciarPac);
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		panel.add(verticalStrut_2);
		
		JButton btnGerenciarSer = new JButton("Gerenciar Serviços");
		// *** ALTERAÇÃO AQUI: Chamar ServicoManagementFrame diretamente ***
		btnGerenciarSer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ServicoManagementFrame servicoFrame = new ServicoManagementFrame();
		        servicoFrame.setVisible(true);
			}
		});
		btnGerenciarSer.setFont(new Font("Verdana", Font.BOLD, 14));
		panel.add(btnGerenciarSer);
		
		Component verticalStrut_3 = Box.createVerticalStrut(20);
		panel.add(verticalStrut_3);
		
		JButton btnContratar = new JButton("Contratar");
		btnContratar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Botão Contratar clicado!");
				// Lógica para abrir a tela de Contratação virá aqui
			}
		});
		btnContratar.setFont(new Font("Verdana", Font.BOLD, 14));
		panel.add(btnContratar);
		
		Component verticalStrut_4 = Box.createVerticalStrut(20);
		panel.add(verticalStrut_4);
		
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnSair.setFont(new Font("Verdana", Font.BOLD, 14));
		panel.add(btnSair);

	}

}
