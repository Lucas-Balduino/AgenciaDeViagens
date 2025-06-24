package com.agencia.view;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ClienteManagementFrame extends JFrame {
    private ClienteNacionalPanel nacionalPanel;
    private ClienteEstrangeiroPanel estrangeiroPanel;
    private ClienteListagemPanel listagemPanel;

    public ClienteManagementFrame() {
        super("Gerenciamento de Clientes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Cria as abas e mantém referência aos painéis
        JTabbedPane tabbedPane = new JTabbedPane();
        nacionalPanel = new ClienteNacionalPanel();
        estrangeiroPanel = new ClienteEstrangeiroPanel();
        listagemPanel = new ClienteListagemPanel(nacionalPanel, estrangeiroPanel, tabbedPane);

        tabbedPane.addTab("Cadastro Nacional", nacionalPanel);
        tabbedPane.addTab("Cadastro Estrangeiro", estrangeiroPanel);
        tabbedPane.addTab("Listagem de Clientes", listagemPanel);

        // Atualiza listagem ao mostrar aba de listagem
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedComponent() == listagemPanel) {
                    listagemPanel.buscarClientes();
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Ajusta tamanho automaticamente
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteManagementFrame frame = new ClienteManagementFrame();
            frame.setVisible(true);
        });
    }
}
