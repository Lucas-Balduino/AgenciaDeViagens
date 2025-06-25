package com.agencia.view.pacote;

import com.agencia.dao.PacoteDao;
import com.agencia.dao.ServicoDao;
import com.agencia.model.Pacote;
import com.agencia.model.Servico;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class PacoteCadastroPanel extends JPanel {
    private JTextField nomeField; 
    private JTextField destinoField;
    private JSpinner duracaoSpinner;
    private JFormattedTextField precoField;
    private JList<Servico> servicosList;
    private JButton salvarButton;
    private JButton limparButton; // Renomeado de cancelar para limpar

    private PacoteDao pacoteDao;
    private ServicoDao servicoDao;

    // Removendo variáveis de controle de edição
    // private boolean editMode = false;
    // private Long editId;

    public PacoteCadastroPanel() {
        pacoteDao = new PacoteDao();
        servicoDao = new ServicoDao();

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Nome do Pacote
        gc.gridx=0; gc.gridy=0;
        add(new JLabel("Nome do Pacote:"), gc);
        nomeField = new JTextField(20);
        gc.gridx=1; gc.gridy=0;
        add(nomeField, gc);

        // Destino
        gc.gridx=0; gc.gridy=1;
        add(new JLabel("Destino:"), gc);
        destinoField = new JTextField(20);
        gc.gridx=1; gc.gridy=1;
        add(destinoField, gc);

        // Duração
        gc.gridx=0; gc.gridy=2;
        add(new JLabel("Duração (dias):"), gc);
        duracaoSpinner = new JSpinner(new SpinnerNumberModel(1,1,365,1));
        gc.gridx=1; gc.gridy=2;
        add(duracaoSpinner, gc);

        // Preço
        gc.gridx=0; gc.gridy=3;
        add(new JLabel("Preço:"), gc);
        NumberFormat nf = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        precoField = new JFormattedTextField(new DefaultFormatterFactory(formatter));
        precoField.setColumns(10);
        gc.gridx=1; gc.gridy=3;
        add(precoField, gc);

        // Serviços
        gc.gridx=0; gc.gridy=4;
        add(new JLabel("Serviços Incluídos:"), gc);
        DefaultListModel<Servico> lm = new DefaultListModel<>();
        try {
            List<Servico> servicos = servicoDao.buscarTodos();
            for (Servico s : servicos) lm.addElement(s);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar serviços: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
        servicosList = new JList<>(lm);
        servicosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane servScroll = new JScrollPane(servicosList);
        servScroll.setPreferredSize(new Dimension(200,100));
        gc.gridx=1; gc.gridy=4;
        add(servScroll, gc);

        // Buttons
        salvarButton = new JButton("Salvar");
        limparButton = new JButton("Limpar"); 
        JPanel btnPanel = new JPanel();
        btnPanel.add(salvarButton);
        btnPanel.add(limparButton);
        gc.gridx=0; gc.gridy=5; gc.gridwidth=2;
        add(btnPanel, gc);

        // Actions
        salvarButton.addActionListener((ActionEvent e) -> salvarPacote()); // Chamada alterada
        limparButton.addActionListener(e -> limparFormulario());
    }

    // Renomeado e simplificado para apenas salvar novos pacotes
    private void salvarPacote() { 
        String nome = nomeField.getText().trim(); 
        String destino = destinoField.getText().trim();
        int duracao = (int) duracaoSpinner.getValue();
        Object p = precoField.getValue();
        List<Servico> selecionados = servicosList.getSelectedValuesList();

        if(nome.isEmpty()) { 
            JOptionPane.showMessageDialog(this, "O nome do pacote é obrigatório.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(destino.isEmpty() || p==null) {
            JOptionPane.showMessageDialog(this, "Destino e preço são obrigatórios.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double preco = ((Number)p).doubleValue();
        if(preco<=0) {
            JOptionPane.showMessageDialog(this, "Preço deve ser > 0.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Pacote pac = new Pacote();
            pac.setNome(nome); 
            pac.setDestino(destino);
            pac.setDuracao(duracao);
            pac.setPreco(preco);
            
            // Sempre insere, pois este painel é só para cadastro
            pacoteDao.inserir(pac);
            
            // Associar serviços ao NOVO pacote
            for(Servico s: selecionados) {
                pacoteDao.adicionarServicoAoPacote(pac.getId(), s.getId());
            }

            JOptionPane.showMessageDialog(this, "Pacote cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar pacote: "+ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); 
        }
    }

    private void limparFormulario() {
        nomeField.setText(""); 
        destinoField.setText("");
        duracaoSpinner.setValue(1);
        precoField.setValue(null);
        servicosList.clearSelection();
        salvarButton.setText("Salvar"); // Sempre "Salvar" neste painel
        // editMode=false; // Removido
        // editId=null; // Removido
    }

    // Método de preenchimento para edição removido, pois esta classe não fará mais edição.
    // public void preencherParaEdicao(Pacote pac) { ... }
}
