package com.agencia.view; // Mantido no pacote 'view' conforme seu ClienteManagementFrame

import com.agencia.view.servicos.ServicoCadastroPanel;
import com.agencia.view.servicos.ServicoListagemPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class ServicoManagementFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private ServicoCadastroPanel cadastroPanel;
    private ServicoListagemPanel listagemPanel;

    public ServicoManagementFrame() {
        super("Gerenciamento de Serviços");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha apenas esta janela, não a aplicação toda

        // Cria o painel de abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Cria os painéis de conteúdo
        // Passa uma referência do método buscarServicos() do listagemPanel para o cadastroPanel
        // para que o cadastroPanel possa notificar o listagemPanel para atualizar a lista
        listagemPanel = new ServicoListagemPanel();
        cadastroPanel = new ServicoCadastroPanel(v -> listagemPanel.buscarServicos()); // Lambda para callback

        // Adiciona os painéis como abas
        tabbedPane.addTab("Cadastro de Serviço", cadastroPanel);
        tabbedPane.addTab("Listagem de Serviços", listagemPanel);

        // Adiciona um listener para atualizar a listagem quando a aba de listagem é selecionada
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Se a aba selecionada for a de listagem, atualiza os dados
                if (tabbedPane.getSelectedComponent() == listagemPanel) {
                    listagemPanel.buscarServicos();
                }
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Ajusta o tamanho da janela para se adequar ao conteúdo
        pack();
        // Centraliza a janela na tela
        setLocationRelativeTo(null);
    }

    /**
     * Método principal para teste individual da janela de gerenciamento de serviços.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServicoManagementFrame frame = new ServicoManagementFrame();
            frame.setVisible(true);
        });
    }
}
