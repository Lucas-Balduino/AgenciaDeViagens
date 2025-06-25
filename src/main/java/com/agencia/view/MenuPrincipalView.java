package com.agencia.view;

import com.agencia.view.ClienteManagementFrame;
import com.agencia.view.ServicoManagementFrame; 
import com.agencia.view.ContratacaoManagementFrame; // Certifique-se de que este import está aqui


import java.awt.EventQueue;
import java.awt.BorderLayout; // *** ADICIONADO ESTE IMPORT ***

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout; 
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.Box;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

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
        setBounds(100, 100, 450, 450); 

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0)); // Linha 53, onde o erro ocorre.
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        JLabel lblNewLabel = new JLabel("Bem-vindo à Agência de Viagens!");
        lblNewLabel.setFont(new Font("Verdana", Font.BOLD, 18));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblNewLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Botão Gerenciar Clientes
        JButton btnGerenciarCli = new JButton("Gerenciar Clientes");
        btnGerenciarCli.addActionListener(e -> {
            ClienteManagementFrame frame = new ClienteManagementFrame();
            frame.setVisible(true);
        });
        btnGerenciarCli.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGerenciarCli.setFont(new Font("Verdana", Font.BOLD, 14));
        mainPanel.add(btnGerenciarCli);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Botão Gerenciar Pacotes
        JButton btnGerenciarPac = new JButton("Gerenciar Pacotes");
        btnGerenciarPac.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PacoteManagementFrame frame = new PacoteManagementFrame();
                frame.setVisible(true);
            }
        });
        btnGerenciarPac.setFont(new Font("Verdana", Font.BOLD, 14));
        btnGerenciarPac.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnGerenciarPac);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Botão Gerenciar Serviços
        JButton btnGerenciarSer = new JButton("Gerenciar Serviços");
        btnGerenciarSer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServicoManagementFrame servicoFrame = new ServicoManagementFrame();
                servicoFrame.setVisible(true);
            }
        });
        btnGerenciarSer.setFont(new Font("Verdana", Font.BOLD, 14));
        btnGerenciarSer.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnGerenciarSer);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Botão Contratar
        JButton btnContratar = new JButton("Contratar");
        btnContratar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ContratacaoManagementFrame contratacaoFrame = new ContratacaoManagementFrame();
                contratacaoFrame.setVisible(true);
            }
        });
        btnContratar.setFont(new Font("Verdana", Font.BOLD, 14));
        btnContratar.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnContratar);
        
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Botão Sair
        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnSair.setFont(new Font("Verdana", Font.BOLD, 14));
        btnSair.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(btnSair);
    }
}
