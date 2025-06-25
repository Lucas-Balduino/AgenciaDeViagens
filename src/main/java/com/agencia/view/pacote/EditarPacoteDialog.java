package com.agencia.view.pacote;

import com.agencia.model.Pacote;
import com.agencia.model.Servico;
import com.agencia.service.PacoteService;
import com.agencia.dao.ServicoDao; // Necessário para carregar serviços na JList

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Scanner; // Para inicializar Services

public class EditarPacoteDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private Pacote pacoteOriginal; // O pacote que está sendo editado
    private PacoteService pacoteService;
    private ServicoDao servicoDao; // Para a JList de serviços

    private JTextField nomeField;
    private JTextField destinoField;
    private JSpinner duracaoSpinner;
    private JFormattedTextField precoField;
    private JList<Servico> servicosList; // JList para serviços
    private JButton salvarButton;
    private JButton cancelarButton;

    private boolean isUpdated = false; // Flag para indicar se a atualização foi bem-sucedida

    /**
     * Construtor para o diálogo de edição de pacote.
     * @param parent O componente pai que abriu este diálogo.
     * @param pacote O objeto Pacote a ser editado.
     */
    public EditarPacoteDialog(Window parent, Pacote pacote) {
        super(parent, "Editar Pacote: " + pacote.getNome(), ModalityType.APPLICATION_MODAL);
        this.pacoteOriginal = pacote;
        this.pacoteService = new PacoteService(new Scanner(System.in)); // Instancia o serviço
        this.servicoDao = new ServicoDao(); // Instancia o DAO de serviço para a JList

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(550, 450); // Aumentei o tamanho para o formulário
        setLocationRelativeTo(parent);

        setLayout(new BorderLayout(10, 10));
        
        JLabel lblTitle = new JLabel("Editar Dados do Pacote");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 16));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 5, 5, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        // Nome do Pacote
        gc.gridx=0; gc.gridy=0;
        formPanel.add(new JLabel("Nome do Pacote:"), gc);
        nomeField = new JTextField(25);
        gc.gridx=1; gc.gridy=0;
        formPanel.add(nomeField, gc);

        // Destino
        gc.gridx=0; gc.gridy=1;
        formPanel.add(new JLabel("Destino:"), gc);
        destinoField = new JTextField(25);
        gc.gridx=1; gc.gridy=1;
        formPanel.add(destinoField, gc);

        // Duração
        gc.gridx=0; gc.gridy=2;
        formPanel.add(new JLabel("Duração (dias):"), gc);
        duracaoSpinner = new JSpinner(new SpinnerNumberModel(1,1,365,1));
        gc.gridx=1; gc.gridy=2;
        formPanel.add(duracaoSpinner, gc);

        // Preço
        gc.gridx=0; gc.gridy=3;
        formPanel.add(new JLabel("Preço:"), gc);
        NumberFormat nf = NumberFormat.getNumberInstance();
        NumberFormatter formatter = new NumberFormatter(nf);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.01);
        formatter.setAllowsInvalid(false);
        precoField = new JFormattedTextField(new DefaultFormatterFactory(formatter));
        precoField.setColumns(10);
        gc.gridx=1; gc.gridy=3;
        formPanel.add(precoField, gc);

        // Serviços Incluídos
        gc.gridx=0; gc.gridy=4;
        formPanel.add(new JLabel("Serviços Incluídos:"), gc);
        DefaultListModel<Servico> lm = new DefaultListModel<>();
        try {
            List<Servico> todosServicos = servicoDao.buscarTodos(); // Carrega todos os serviços disponíveis
            for (Servico s : todosServicos) lm.addElement(s);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar serviços disponíveis: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
        servicosList = new JList<>(lm);
        servicosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane servScroll = new JScrollPane(servicosList);
        servScroll.setPreferredSize(new Dimension(250, 120)); // Tamanho ajustado para visibilidade
        gc.gridx=1; gc.gridy=4;
        formPanel.add(servScroll, gc);

        add(formPanel, BorderLayout.CENTER);

        // Painel de Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        salvarButton = new JButton("Salvar Alterações");
        cancelarButton = new JButton("Cancelar");

        buttonPanel.add(salvarButton);
        buttonPanel.add(cancelarButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Preencher formulário com dados do pacote original
        preencherFormulario();

        // Adicionar Action Listeners
        salvarButton.addActionListener(e -> salvarAlteracoes());
        cancelarButton.addActionListener(e -> dispose());
    }

    /**
     * Preenche os campos do formulário com os dados do pacote original.
     */
    private void preencherFormulario() {
        nomeField.setText(pacoteOriginal.getNome());
        destinoField.setText(pacoteOriginal.getDestino());
        duracaoSpinner.setValue(pacoteOriginal.getDuracao());
        precoField.setValue(pacoteOriginal.getPreco());

        // Selecionar os serviços que já estão ligados a este pacote na JList
        DefaultListModel<Servico> lm = (DefaultListModel<Servico>) servicosList.getModel();
        try {
            List<Servico> ligados = pacoteService.buscarServicosDoPacoteGUI(pacoteOriginal.getId());
            int[] indicesParaSelecionar = new int[ligados.size()];
            int count = 0; 
            for (Servico sLigado : ligados) { 
                for (int j = 0; j < lm.getSize(); j++) { 
                    if (lm.getElementAt(j).getId().equals(sLigado.getId())) {
                        indicesParaSelecionar[count++] = j; 
                        break; 
                    }
                }
            }
            int[] finalIndices = new int[count];
            System.arraycopy(indicesParaSelecionar, 0, finalIndices, 0, count);
            servicosList.setSelectedIndices(finalIndices);
            
        } catch(Exception e) { // Usamos Exception pois PacoteService.buscarServicosDoPacoteGUI lança Exception
            JOptionPane.showMessageDialog(this, "Erro ao carregar serviços do pacote para edição: "+e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }

    /**
     * Salva as alterações do pacote no banco de dados.
     */
    private void salvarAlteracoes() {
        String novoNome = nomeField.getText().trim();
        String novoDestino = destinoField.getText().trim();
        int novaDuracao = (int) duracaoSpinner.getValue();
        Object p = precoField.getValue(); // Preço como Object, pode ser null

        // Validações básicas
        if (novoNome.isEmpty() || novoDestino.isEmpty() || p == null) {
            JOptionPane.showMessageDialog(this,
                    "Nome, Destino e Preço são obrigatórios.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double novoPreco = ((Number) p).doubleValue();
        if (novoPreco <= 0) {
            JOptionPane.showMessageDialog(this, "Preço deve ser maior que 0.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cria um novo objeto Pacote com os dados atualizados para passar ao serviço
        Pacote pacoteAtualizado = new Pacote();
        pacoteAtualizado.setId(pacoteOriginal.getId()); // MANTÉM O ID ORIGINAL
        pacoteAtualizado.setNome(novoNome);
        pacoteAtualizado.setDestino(novoDestino);
        pacoteAtualizado.setDuracao(novaDuracao);
        pacoteAtualizado.setPreco(novoPreco);

        List<Servico> servicosSelecionados = servicosList.getSelectedValuesList();

        try {
            // Atualiza os dados principais do pacote
            pacoteService.atualizarPacoteGUI(pacoteAtualizado);

            // Lógica para sincronizar serviços: remove os atuais e adiciona os selecionados
            // Mais robusto: comparar listas e fazer apenas as adições/remoções necessárias
            List<Servico> servicosAtuaisNoBd = pacoteService.buscarServicosDoPacoteGUI(pacoteAtualizado.getId());
            
            // Remove serviços que não estão mais selecionados
            for (Servico sAtual : servicosAtuaisNoBd) {
                if (!servicosSelecionados.contains(sAtual)) { // Supondo que Servico.equals/hashCode está bem implementado por ID
                    pacoteService.removerServicoDoPacoteGUI(pacoteAtualizado.getId(), sAtual.getId());
                }
            }
            // Adiciona serviços que foram selecionados e não estão no BD
            for (Servico sSelecionado : servicosSelecionados) {
                if (!servicosAtuaisNoBd.contains(sSelecionado)) { // Supondo que Servico.equals/hashCode está bem implementado por ID
                    pacoteService.adicionarServicoAoPacoteGUI(pacoteAtualizado.getId(), sSelecionado.getId());
                }
            }


            JOptionPane.showMessageDialog(this,
                    "Pacote atualizado com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            isUpdated = true; // Define a flag de atualização
            dispose(); // Fecha o diálogo
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar alterações do pacote: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Retorna se o pacote foi atualizado com sucesso neste diálogo.
     * @return true se o pacote foi atualizado, false caso contrário.
     */
    public boolean isUpdated() {
        return isUpdated;
    }
}
