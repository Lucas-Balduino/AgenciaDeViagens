package com.agencia.view.cliente;

import com.agencia.model.Cliente;
import com.agencia.model.Nacional;
import com.agencia.model.Estrangeiro;
import com.agencia.service.ClienteService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner; // Necessário para o construtor do ClienteService

public class EditarClienteDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Cliente clienteOriginal; // Cliente que está sendo editado
    private ClienteService clienteService;

    private JTextField txtNome;
    private JTextField txtTelefone;
    private JTextField txtEmail;
    private JTextField txtDocumento; // Para exibir CPF/Passaporte (não editável)
    private JLabel lblTipoCliente; // Para exibir se é Nacional/Estrangeiro

    private boolean isUpdated = false; // Flag para indicar se o cliente foi atualizado

    /**
     * Construtor para o diálogo de edição de cliente.
     * @param parent O JFrame pai que abriu este diálogo.
     * @param cliente O objeto Cliente a ser editado.
     */
    public EditarClienteDialog(Window parent, Cliente cliente) {
        super(parent, "Editar Cliente: " + cliente.getNome(), ModalityType.APPLICATION_MODAL);
        this.clienteOriginal = cliente;
        this.clienteService = new ClienteService(new Scanner(System.in)); // Instancia o serviço

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai

        setLayout(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("Editar Dados do Cliente");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Espaçamento interno
        gbc.fill = GridBagConstraints.HORIZONTAL; // Preenche o espaço horizontal

        // Tipo de Cliente (Não editável)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Tipo de Cliente:"), gbc);
        lblTipoCliente = new JLabel();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(lblTipoCliente, gbc);

        // Nome
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(txtNome, gbc);

        // Telefone
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Telefone:"), gbc);
        txtTelefone = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(txtTelefone, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(txtEmail, gbc);

        // Documento (CPF/Passaporte) - Não editável
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Documento:"), gbc);
        txtDocumento = new JTextField(20);
        txtDocumento.setEditable(false); // Impede edição do documento
        txtDocumento.setBackground(SystemColor.control); // Cor de fundo para indicar não-editável
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(txtDocumento, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnSalvar = new JButton("Salvar Alterações");
        JButton btnCancelar = new JButton("Cancelar");

        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnCancelar);
        add(buttonPanel, BorderLayout.SOUTH);

        // Preencher formulário com dados do cliente original
        preencherFormulario();

        // Adicionar Action Listeners
        btnSalvar.addActionListener(e -> salvarAlteracoes());
        btnCancelar.addActionListener(e -> dispose());
    }

    /**
     * Preenche os campos do formulário com os dados do cliente original.
     */
    private void preencherFormulario() {
        if (clienteOriginal instanceof Nacional) {
            lblTipoCliente.setText("Nacional");
            txtDocumento.setText(((Nacional) clienteOriginal).getCpf());
        } else if (clienteOriginal instanceof Estrangeiro) {
            lblTipoCliente.setText("Estrangeiro");
            txtDocumento.setText(((Estrangeiro) clienteOriginal).getPassaporte());
        }
        txtNome.setText(clienteOriginal.getNome());
        txtTelefone.setText(clienteOriginal.getTelefone());
        txtEmail.setText(clienteOriginal.getEmail());
    }

    /**
     * Salva as alterações do cliente no banco de dados.
     */
    private void salvarAlteracoes() {
        String novoNome = txtNome.getText().trim();
        String novoTelefone = txtTelefone.getText().trim();
        String novoEmail = txtEmail.getText().trim();

        // Validações básicas
        if (novoNome.isEmpty() || novoTelefone.isEmpty() || novoEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nome, Telefone e Email são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!novoEmail.matches("^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")) {
            JOptionPane.showMessageDialog(this,
                    "E-mail em formato inválido.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria um novo objeto Cliente com os dados atualizados para passar ao serviço
        Cliente clienteAtualizado;
        if (clienteOriginal instanceof Nacional) {
            clienteAtualizado = new Nacional(novoNome, novoTelefone, novoEmail, ((Nacional) clienteOriginal).getCpf());
            clienteAtualizado.setId(clienteOriginal.getId()); // Mantém o ID original
        } else if (clienteOriginal instanceof Estrangeiro) {
            clienteAtualizado = new Estrangeiro(novoNome, novoTelefone, novoEmail, ((Estrangeiro) clienteOriginal).getPassaporte());
            clienteAtualizado.setId(clienteOriginal.getId()); // Mantém o ID original
        } else {
            JOptionPane.showMessageDialog(this, "Tipo de cliente desconhecido.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean sucesso = clienteService.atualizarClienteGUI(clienteAtualizado);
            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                        "Cliente atualizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                isUpdated = true; // Define a flag de atualização
                dispose(); // Fecha o diálogo
            } else {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma alteração foi salva ou cliente não encontrado.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar alterações: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Retorna se o cliente foi atualizado com sucesso neste diálogo.
     * @return true se o cliente foi atualizado, false caso contrário.
     */
    public boolean isUpdated() {
        return isUpdated;
    }
}
