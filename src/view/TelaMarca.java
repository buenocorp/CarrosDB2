package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.MarcaController;
import dao.MarcaDAO;
import model.Marca;

public class TelaMarca extends JDialog {

    private JPanel contentPane;

    private JTextField txtNome;
    private JTextField txtPais;

    private JTable tabela;
    private DefaultTableModel modeloTabela;

    private MarcaController controller;

    private int idSelecionado = 0;
    private Marca marcaSelecionada;

    private boolean modoSelecao;
    
    public TelaMarca(java.awt.Window parent) {
        this(parent, false);
    }

    public TelaMarca(java.awt.Window parent, boolean modoSelecao) {
        super(parent, ModalityType.APPLICATION_MODAL);

        try {
			controller = new MarcaController(new MarcaDAO());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        this.modoSelecao = modoSelecao;

        configurarJanela();
        criarComponentes();
        configurarModo();
        atualizarTabela();
    }
    
    public TelaMarca(java.awt.Window parent, MarcaController controller) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.modoSelecao = false;
        configurarJanela();
        criarComponentes();
        configurarModo();
        atualizarTabela();
    }

    public static Marca selecionarMarca(java.awt.Window parent) {
        TelaMarca tela = new TelaMarca(parent, true);
        tela.setVisible(true);
        return tela.getMarcaSelecionada();
    }

    private void configurarJanela() {
        setTitle(modoSelecao ? "Selecionar Marca" : "Cadastro de Marca");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(100, 100, 520, 420);
        setLocationRelativeTo(getParent());

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(null);
        setContentPane(contentPane);
    }

    private void criarComponentes() {

        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(20, 20, 80, 20);
        contentPane.add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(100, 20, 200, 25);
        contentPane.add(txtNome);

        JLabel lblPais = new JLabel("País:");
        lblPais.setBounds(20, 60, 80, 20);
        contentPane.add(lblPais);

        txtPais = new JTextField();
        txtPais.setBounds(100, 60, 200, 25);
        contentPane.add(txtPais);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(330, 20, 140, 30);
        contentPane.add(btnSalvar);
        btnSalvar.addActionListener(e -> salvarMarca());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(330, 60, 140, 30);
        contentPane.add(btnEditar);
        btnEditar.addActionListener(e -> carregarParaEdicao());

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(330, 100, 140, 30);
        contentPane.add(btnExcluir);
        btnExcluir.addActionListener(e -> excluirMarca());

        JButton btnSelecionar = new JButton("Selecionar");
        btnSelecionar.setBounds(180, 340, 140, 30);
        contentPane.add(btnSelecionar);
        btnSelecionar.addActionListener(e -> selecionarMarca());

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("País");

        tabela = new JTable(modeloTabela);

        // esconder ID
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(20, 150, 460, 180);
        contentPane.add(scroll);

        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && modoSelecao) {
                    selecionarMarca();
                }
            }
        });
    }

    private void configurarModo() {

        if (modoSelecao) {
            txtNome.setEnabled(false);
            txtPais.setEnabled(false);
        }
    }

    private void salvarMarca() {

        String nome = txtNome.getText();
        String pais = txtPais.getText();

        if (nome.isEmpty() || pais.isEmpty()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Preencha todos os campos!");
            return;
        }

        if (idSelecionado == 0) {
            controller.salvarMarca(nome, pais);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Marca salva!");
        } else {
            controller.atualizarMarca(idSelecionado, nome, pais);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Marca atualizada!");
            idSelecionado = 0;
        }

        limparCampos();
        atualizarTabela();
    }

    private void carregarParaEdicao() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Selecione uma marca!");
            return;
        }

        idSelecionado = (int) tabela.getValueAt(linha, 0);

        Marca marca = controller.buscarMarca(idSelecionado);

        txtNome.setText(marca.getNome());
        txtPais.setText(marca.getPais());
    }

    private void excluirMarca() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Selecione uma marca!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);

        controller.excluirMarca(id);

        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Marca excluída!");

        atualizarTabela();
    }

    private void selecionarMarca() {

        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Selecione uma marca!");
            return;
        }

        int id = (int) tabela.getValueAt(linha, 0);

        marcaSelecionada = controller.buscarMarca(id);

        dispose(); // fecha a tela
    }

    private void atualizarTabela() {

        modeloTabela.setRowCount(0);

        for (Marca m : controller.listarMarcas()) {
            modeloTabela.addRow(new Object[]{
                    m.getId(),
                    m.getNome(),
                    m.getPais()
            });
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtPais.setText("");
        idSelecionado = 0;
    }

    public Marca getMarcaSelecionada() {
        return marcaSelecionada;
    }

    public void atualizar() {
        atualizarTabela();
    }
}