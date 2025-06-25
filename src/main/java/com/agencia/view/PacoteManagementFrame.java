package com.agencia.view;

import com.agencia.view.pacote.*;

import javax.swing.*;
import java.awt.*;

public class PacoteManagementFrame extends JFrame {
    private PacoteCadastroPanel cadastroPanel;
    private PacoteListagemPanel listagemPanel;
    private JTabbedPane tabs;
    private boolean errorOnInit;

    public PacoteManagementFrame() {
        super("Gerenciamento de Pacotes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        tabs = new JTabbedPane();
        errorOnInit = false;
        JPanel errorPanel;

        // Cadastro Tab
        try {
            cadastroPanel = new PacoteCadastroPanel();
            tabs.addTab("Cadastro de Pacote", cadastroPanel);
        } catch (Exception ex) {
            errorOnInit = true;
            errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(new JLabel("Erro ao carregar formulário de cadastro: " + ex.getMessage(), SwingConstants.CENTER), BorderLayout.CENTER);
            tabs.addTab("Cadastro de Pacote", errorPanel);
        }

        // Listagem Tab
        try {
            listagemPanel = new PacoteListagemPanel(cadastroPanel, tabs);
            tabs.addTab("Listagem de Pacotes", listagemPanel);
        } catch (Exception ex) {
            errorOnInit = true;
            errorPanel = new JPanel(new BorderLayout());
            errorPanel.add(new JLabel("Erro ao carregar listagem de pacotes: " + ex.getMessage(), SwingConstants.CENTER), BorderLayout.CENTER);
            tabs.addTab("Listagem de Pacotes", errorPanel);
        }

        // Atualiza listagem ao trocar de aba
        tabs.addChangeListener(e -> {
            if (!errorOnInit && listagemPanel != null && tabs.getSelectedComponent() == listagemPanel) {
                listagemPanel.buscarPacotes();
            }
        });

        add(tabs, BorderLayout.CENTER);

        pack();
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PacoteManagementFrame frame = new PacoteManagementFrame();
            frame.setVisible(true);
        });
    }
}
