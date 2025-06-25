package com.agencia.view.servicos;

import com.agencia.model.Servico;
import com.agencia.service.ServicoService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner; // Necessário porque ServicoService ainda tem construtor com Scanner
import java.util.function.Consumer; // Para o callback de atualização

public class ServicoCadastroPanel extends JPanel {
    private JTextField nomeField;
    private JTextField duracaoField;
    private JTextField descricaoField;
    private JButton salvarButton;
    private JButton limparButton; // Renomeado de cancelar para limpar

    private ServicoService servicoService;
    private Consumer<Void> refreshListCallback; // Callback para notificar a listagem para atualizar

    /**
     * Construtor para o painel de cadastro de serviços.
     * @param refreshListCallback Um callback para ser executado após o sucesso do cadastro,
     * geralmente para atualizar a lista de serviços.
     */
    public ServicoCadastroPanel(Consumer<Void> refreshListCallback) {
        // Inicializa o ServicoService. Em uma aplicação real, considere injeção de dependência.
        this.servicoService = new ServicoService(new Scanner(System.in));
        this.refreshListCallback = refreshListCallback;

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8); // Espaçamento entre os componentes
        gc.fill = GridBagConstraints.HORIZONTAL; // Preenche horizontalmente

        // Configuração de Título
        JLabel titleLabel = new JLabel("CADASTRO DE NOVO SERVIÇO");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2; // Ocupa duas colunas
        gc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gc);

        // Reset gridwidth para os campos
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.WEST; // Alinha labels à esquerda

        // Campo Nome
        gc.gridx = 0; gc.gridy = 1;
        add(new JLabel("Nome do Serviço:"), gc);
        nomeField = new JTextField(25); // Tamanho sugerido
        gc.gridx = 1; gc.gridy = 1;
        add(nomeField, gc);

        // Campo Duração
        gc.gridx = 0; gc.gridy = 2;
        add(new JLabel("Duração (dias/horas):"), gc);
        duracaoField = new JTextField(15);
        gc.gridx = 1; gc.gridy = 2;
        add(duracaoField, gc);

        // Campo Descrição (JTextArea para múltiplas linhas)
        gc.gridx = 0; gc.gridy = 3;
        add(new JLabel("Descrição:"), gc);
        descricaoField = new JTextField(30); // Usando JTextField por simplicidade, ou JTextArea para textos longos
        gc.gridx = 1; gc.gridy = 3;
        add(descricaoField, gc);

        // Painel de Botões
        salvarButton = new JButton("Salvar");
        limparButton = new JButton("Limpar Campos"); // Renomeado
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(salvarButton);
        buttonPanel.add(limparButton);

        gc.gridx = 0; gc.gridy = 4;
        gc.gridwidth = 2; // Ocupa duas colunas para centralizar
        gc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gc);

        // Adiciona Listeners aos botões
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarServico();
            }
        });

        limparButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });
    }

    private void salvarServico() {
        String nome = nomeField.getText().trim();
        String duracaoStr = duracaoField.getText().trim();
        String descricao = descricaoField.getText().trim();
        int duracao = 0;

        // Validação da duração
        if (duracaoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "O campo 'Duração' é obrigatório.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            duracao = Integer.parseInt(duracaoStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, insira um número válido para a duração (apenas números inteiros).",
                    "Erro de Entrada",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Outras validações (delegando ao Service, mas com feedback UI)
        try {
            servicoService.cadastrarServico(nome, duracao, descricao);
            JOptionPane.showMessageDialog(this,
                    "Serviço cadastrado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            // Chama o callback para atualizar a lista
            if (refreshListCallback != null) {
                refreshListCallback.accept(null);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro de Validação: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar serviço: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Para depuração
        }
    }

    /**
     * Limpa todos os campos do formulário.
     */
    public void limparFormulario() {
        nomeField.setText("");
        duracaoField.setText("");
        descricaoField.setText("");
    }
}
