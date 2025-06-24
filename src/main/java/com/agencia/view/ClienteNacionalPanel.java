package com.agencia.view;

import com.agencia.dao.*;
import com.agencia.model.Nacional;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClienteNacionalPanel extends JPanel {
    private JTextField nomeField;
    private JTextField telefoneField;
    private JTextField emailField;
    private JFormattedTextField cpfField;
    private JButton salvarButton;
    private JButton cancelarButton;

    private ClienteDao clienteDao;

    public ClienteNacionalPanel() {
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

        // CPF
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            cpfField = new JFormattedTextField(cpfMask);
        } catch (Exception e) {
            cpfField = new JFormattedTextField();
        }
        cpfField.setColumns(10);
        gc.gridx = 0; gc.gridy = 3;
        add(new JLabel("CPF:"), gc);
        gc.gridx = 1; gc.gridy = 3;
        add(cpfField, gc);

        // Botões
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        gc.gridx = 0; gc.gridy = 4; gc.gridwidth = 2;
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
        String cpf = cpfField.getText().replaceAll("\\D", "").trim();

        if (nome.isEmpty() || telefone.isEmpty() || email.isEmpty() || cpf.length() != 11) {
            JOptionPane.showMessageDialog(this,
                    "Todos os campos são obrigatórios e CPF deve ter 11 dígitos.",
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
        if (!validaCPF(cpf)) {
            JOptionPane.showMessageDialog(this,
                    "CPF inválido.",
                    "Erro de Validação",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Nacional n = new Nacional();
            n.setNome(nome);
            n.setTelefone(telefone);
            n.setEmail(email);
            n.setCpf(cpf);
            clienteDao.inserir(n);
            JOptionPane.showMessageDialog(this,
                    "Cliente nacional cadastrado com sucesso!",
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
        cpfField.setValue(null);
    }

    private boolean validaCPF(String cpf) {
        if (cpf == null || cpf.matches("^0{11}$")) return false;
        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) sum += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
            int first = 11 - (sum % 11);
            if (first >= 10) first = 0;
            if (first != Character.getNumericValue(cpf.charAt(9))) return false;
            sum = 0;
            for (int i = 0; i < 10; i++) sum += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
            int second = 11 - (sum % 11);
            if (second >= 10) second = 0;
            return second == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean editMode = false;
    private Long editId;

    public void preencherParaEdicao(Nacional nacional) {
        nomeField.setText(nacional.getNome());
        telefoneField.setText(nacional.getTelefone());
        emailField.setText(nacional.getEmail());
        cpfField.setText(nacional.getCpf());

        this.editId = nacional.getId();
        this.editMode = true;
        salvarButton.setText("Atualizar");
    }


}
