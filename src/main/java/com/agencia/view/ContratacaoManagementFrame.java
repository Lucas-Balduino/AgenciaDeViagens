package com.agencia.view;

import com.agencia.view.contratacao.ContratarPacoteClientePanel;
import com.agencia.view.contratacao.ContratarServicoPacotePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ContratacaoManagementFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private ContratarPacoteClientePanel contratarPacoteClientePanel;
    private ContratarServicoPacotePanel contratarServicoPacotePanel;

    /**
     * Método principal para iniciar a aplicação (apenas para teste de UI independente).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ContratacaoManagementFrame frame = new ContratacaoManagementFrame();
            frame.setVisible(true);
        });
    }

    /**
     * Cria o frame de gerenciamento de contratações.
     */
    public ContratacaoManagementFrame() {
        super("Gerenciamento de Contratações");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela
        setSize(1000, 700); // Tamanho inicial
        setLocationRelativeTo(null); // Centraliza na tela

        // Cria o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Cria os painéis de conteúdo para cada aba
        contratarPacoteClientePanel = new ContratarPacoteClientePanel(); // Nova versão deste painel
        contratarServicoPacotePanel = new ContratarServicoPacotePanel();

        // Adiciona os painéis como abas
        tabbedPane.addTab("Contratar Pacote (Cliente)", contratarPacoteClientePanel);
        tabbedPane.addTab("Contratar Serviços (Pacote)", contratarServicoPacotePanel);

        // Adiciona um listener para atualizar as abas ao serem selecionadas
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedComponent() == contratarPacoteClientePanel) {
                    // Agora chama o método correto para carregar clientes
                    contratarPacoteClientePanel.loadClientesIntoTable(); 
                } else if (tabbedPane.getSelectedComponent() == contratarServicoPacotePanel) {
                    contratarServicoPacotePanel.loadPackagesIntoTable();
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }
}
