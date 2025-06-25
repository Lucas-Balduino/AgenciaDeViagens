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
    private JTextField destinoField;
    private JSpinner duracaoSpinner;
    private JFormattedTextField precoField;
    private JList<Servico> servicosList;
    private JButton salvarButton;
    private JButton cancelarButton;

    private PacoteDao pacoteDao;
    private ServicoDao servicoDao;

    private boolean editMode = false;
    private Long editId;

    public PacoteCadastroPanel() {
        pacoteDao = new PacoteDao();
        servicoDao = new ServicoDao();

        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5,5,5,5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Destino
        gc.gridx=0; gc.gridy=0;
        add(new JLabel("Destino:"), gc);
        destinoField = new JTextField(20);
        gc.gridx=1;
        add(destinoField, gc);

        // Duração
        gc.gridx=0; gc.gridy=1;
        add(new JLabel("Duração (dias):"), gc);
        duracaoSpinner = new JSpinner(new SpinnerNumberModel(1,1,365,1));
        gc.gridx=1;
        add(duracaoSpinner, gc);

        // Preço
        gc.gridx=0; gc.gridy=2;
        add(new JLabel("Preço:"), gc);
        NumberFormat nf = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        precoField = new JFormattedTextField(new DefaultFormatterFactory(formatter));
        precoField.setColumns(10);
        gc.gridx=1;
        add(precoField, gc);

        // Serviços
        gc.gridx=0; gc.gridy=3;
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
        gc.gridx=1;
        add(servScroll, gc);

        // Buttons
        salvarButton = new JButton("Salvar");
        cancelarButton = new JButton("Cancelar");
        JPanel btnPanel = new JPanel();
        btnPanel.add(salvarButton);
        btnPanel.add(cancelarButton);
        gc.gridx=0; gc.gridy=4; gc.gridwidth=2;
        add(btnPanel, gc);

        // Actions
        salvarButton.addActionListener((ActionEvent e) -> salvarOuAtualizar());
        cancelarButton.addActionListener(e -> limparFormulario());
    }

    private void salvarOuAtualizar() {
        String destino = destinoField.getText().trim();
        int duracao = (int) duracaoSpinner.getValue();
        Object p = precoField.getValue();
        List<Servico> selecionados = servicosList.getSelectedValuesList();

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
            pac.setDestino(destino);
            pac.setDuracao(duracao);
            pac.setPreco(preco);
            if(editMode) {
                pac.setId(editId);
                pacoteDao.atualizar(pac);
                salvarButton.setText("Salvar");
                editMode=false;
            } else {
                pacoteDao.inserir(pac);
            }
            // associar serviços: remove todos e adiciona
            List<Servico> atuais = pacoteDao.buscarServicosDoPacote(pac.getId());
            for(Servico s : atuais) {
                pacoteDao.removerServicoDoPacote(pac.getId(), s.getId());
            }
            for(Servico s: selecionados) {
                pacoteDao.adicionarServicoAoPacote(pac.getId(), s.getId());
            }
            JOptionPane.showMessageDialog(this, "Pacote salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar pacote: "+ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        destinoField.setText("");
        duracaoSpinner.setValue(1);
        precoField.setValue(null);
        servicosList.clearSelection();
        salvarButton.setText("Salvar");
        editMode=false;
        editId=null;
    }

    /**
     * Preenche o formulário para edição.
     */
    public void preencherParaEdicao(Pacote pac) {
        destinoField.setText(pac.getDestino());
        duracaoSpinner.setValue(pac.getDuracao());
        precoField.setValue(pac.getPreco());
        // selecionar serviços
        DefaultListModel<Servico> lm = (DefaultListModel<Servico>) servicosList.getModel();
        try {
            List<Servico> ligados = pacoteDao.buscarServicosDoPacote(pac.getId());
            int[] indices = new int[ligados.size()];
            for (int i = 0; i < ligados.size(); i++) {
                indices[i] = lm.indexOf(ligados.get(i));
            }
            servicosList.setSelectedIndices(indices);
        } catch(SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar serviços do pacote: "+e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        editMode=true;
        editId=pac.getId();
        salvarButton.setText("Atualizar");
    }
}
