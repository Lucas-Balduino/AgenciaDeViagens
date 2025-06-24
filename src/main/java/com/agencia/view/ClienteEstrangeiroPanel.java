package com.agencia.view;

import com.agencia.dao.ClienteDao;
import com.agencia.model.Estrangeiro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClienteEstrangeiroPanel extends JPanel {
    private JTextField nomeField;
    private JTextField telefoneField;
    private JTextField emailField;
    private JTextField passaporteField;
    private JButton salvarButton;
    private JButton cancelarButton;

    private ClienteDao clienteDao;

    public ClienteEstrangeiroPanel() {
        clienteDao = new ClienteDao();
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Nome completo
        gc.gridx = 0; gc.gridy = 0;
        add(new JLabel("Nome Completo:"), gc);
        nomeField = new JTextField(20);
        gc.gridx = 1; gc.gridy = 0;
        add(nomeField, gc);

        // Telefone
        gc.gridx = 0; gc.gridy = 1;
        add(new JLabel("Telefone:"), gc);
        telefoneField = new JTextField(15);
        gc.gridx = 1; gc.gridy = 1;
        add(telefoneField, gc);

        // E-mail
        gc.gridx = 0; gc.gridy = 2;
        add(new JLabel("E-mail:"), gc);
        emailField = new JTextField(20);
        gc.gridx = 1; gc.gridy = 2;
        add(emailField, gc);

        // Passaporte
        gc.gridx = 0; gc.gridy = 4;
        add(new JLabel("Passaporte:"), gc);
        passaporteField = new JTextField(20);
        gc.gridx = 1; gc.gridy = 4;
        add(passaporteField, gc);

        // Botões
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        add(buttonPanel, gc);

        // Ações
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarCliente();
            }
        });

        cancelarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparFormulario();
            }
        });
    }

    private void salvarCliente() {
        String nome = nomeField.getText().trim();
        String telefone = telefoneField.getText().trim();
        String email = emailField.getText().trim();
        String passaporte = passaporteField.getText().trim();

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty()|| passaporte.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Todos os campos são obrigatórios.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            JOptionPane.showMessageDialog(this,
                    "E-mail em formato inválido.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!passaporte.matches("^(?!^0+$)[A-Za-z0-9]{3,20}$")) {
            JOptionPane.showMessageDialog(this,
                    "Passaporte inválido. Deve conter entre 3 e 20 caracteres alfanuméricos e não pode ser composto apenas por zeros.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Estrangeiro e = new Estrangeiro();
            e.setNome(nome);
            e.setTelefone(telefone);
            e.setEmail(email);
            e.setPassaporte(passaporte);
            clienteDao.inserir(e);
            JOptionPane.showMessageDialog(this,
                    "Cliente estrangeiro cadastrado com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao cadastrar cliente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        nomeField.setText("");
        telefoneField.setText("");
        emailField.setText("");
        passaporteField.setText("");
    }

    public void preencherParaEdicao(Estrangeiro estrangeiro) {
        nomeField.setText(estrangeiro.getNome());
        telefoneField.setText(estrangeiro.getTelefone());
        emailField.setText(estrangeiro.getEmail());
        passaporteField.setText(estrangeiro.getPassaporte());
        // Também configure modo de edição se necessário
    }

}
