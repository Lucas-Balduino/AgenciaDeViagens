package com.agencia.view;

import com.agencia.view.pacote.PacoteCadastroPanel;
import com.agencia.view.pacote.PacoteListagemPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class PacoteManagementFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private PacoteCadastroPanel cadastroPanel;
    private PacoteListagemPanel listagemPanel; 

    public PacoteManagementFrame() {
        super("Gerenciamento de Pacotes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700); 
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Cria os painéis de conteúdo para cada aba
        cadastroPanel = new PacoteCadastroPanel();
        
        // *** LINHA CORRIGIDA AQUI: Chamar o construtor sem argumentos ***
        listagemPanel = new PacoteListagemPanel(); 

        // Adiciona os painéis como abas
        tabbedPane.addTab("Cadastro de Pacote", cadastroPanel);
        tabbedPane.addTab("Listagem de Pacotes", listagemPanel);

        // Adiciona um listener para atualizar a listagem quando a aba de listagem é selecionada
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedComponent() == listagemPanel) {
                    listagemPanel.buscarPacotes(); // Recarrega os pacotes ao exibir a aba
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PacoteManagementFrame frame = new PacoteManagementFrame();
            frame.setVisible(true);
        });
    }
}
