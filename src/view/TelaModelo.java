package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.ModeloController;
import model.Marca;
import model.Modelo;

public class TelaModelo extends JDialog {

    private JPanel contentPane;
    private JTextField txtModelo;
    private JComboBox<Marca> comboMarca;

    private JTable tabelaModelos;
    private DefaultTableModel modeloTabela;

    private ModeloController controller = new ModeloController();

    private int idSelecionado = 0; 

    public TelaModelo(JFrame parent) {
        super(parent, true);
        configurarJanela();
        criarComponentes();
        carregarMarcas();
        comboMarca.setSelectedIndex(-1);
        atualizarTabela();
    }

    private void configurarJanela() {
        setTitle("Cadastro de Modelo");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 420);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(null);
        setContentPane(contentPane);
    }

    private void criarComponentes() {

        JLabel lblModelo = new JLabel("Modelo:");
        lblModelo.setBounds(30, 30, 80, 20);
        contentPane.add(lblModelo);

        txtModelo = new JTextField();
        txtModelo.setBounds(120, 30, 200, 25);
        contentPane.add(txtModelo);

        JLabel lblMarca = new JLabel("Marca:");
        lblMarca.setBounds(30, 80, 80, 20);
        contentPane.add(lblMarca);

        comboMarca = new JComboBox<>();
        comboMarca.setBounds(120, 80, 200, 25);
        contentPane.add(comboMarca);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(350, 30, 100, 30);
        contentPane.add(btnSalvar);
        btnSalvar.addActionListener(e -> salvarModelo());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(350, 70, 100, 30);
        contentPane.add(btnEditar);
        btnEditar.addActionListener(e -> carregarParaEdicao());

        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(350, 110, 100, 30);
        contentPane.add(btnExcluir);
        btnExcluir.addActionListener(e -> excluirModelo());

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabela.addColumn("ID"); 
        modeloTabela.addColumn("Modelo");
        modeloTabela.addColumn("Marca");

        tabelaModelos = new JTable(modeloTabela);

        // esconder ID
        tabelaModelos.getColumnModel().getColumn(0).setMinWidth(0);
        tabelaModelos.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabelaModelos);
        scroll.setBounds(30, 160, 420, 180);
        contentPane.add(scroll);
    }

    private void carregarMarcas() {
        comboMarca.removeAllItems();
        for (Marca m : controller.listarMarcas()) {
            comboMarca.addItem(m);
        }
    }

    // 🔹 SALVAR (INSERT ou UPDATE)
    private void salvarModelo() {

        String nomeModelo = txtModelo.getText();
        Marca marcaSelecionada = (Marca) comboMarca.getSelectedItem();

        if (nomeModelo.isEmpty() || marcaSelecionada == null) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Preencha todos os campos!");
            return;
        }

        if (idSelecionado == 0) {
            controller.salvarModelo(nomeModelo, marcaSelecionada);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Modelo salvo!");
        } else {
            controller.atualizarModelo(idSelecionado, nomeModelo, marcaSelecionada);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Modelo atualizado!");
            idSelecionado = 0;
        }

        limparCampos();
        atualizarTabela();
    }

    // 🔹 CARREGAR PARA EDIÇÃO
    private void carregarParaEdicao() {

        int linha = tabelaModelos.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Selecione um modelo!");
            return;
        }

        idSelecionado = (int) tabelaModelos.getValueAt(linha, 0);

        Modelo modelo = controller.buscarModelo(idSelecionado);

        txtModelo.setText(modelo.getNome());
        for (int i = 0; i < comboMarca.getItemCount(); i++) {
            Marca m = comboMarca.getItemAt(i);

            if (m.getId() == modelo.getMarca().getId()) {
                comboMarca.setSelectedIndex(i);
                break;
            }
        }
    }

    // 🔹 EXCLUIR
    private void excluirModelo() {

        int linha = tabelaModelos.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Selecione um modelo!");
            return;
        }

        int id = (int) tabelaModelos.getValueAt(linha, 0);

        controller.excluirModelo(id);

        JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane), "Modelo excluído!");

        atualizarTabela();
    }

    // 🔹 ATUALIZAR TABELA
    private void atualizarTabela() {

        modeloTabela.setRowCount(0);

        for (Modelo m : controller.listarModelos()) {
            modeloTabela.addRow(new Object[]{
                    m.getId(),
                    m.getNome(),
                    m.getMarca().getNome()
            });
        }
    }

    private void limparCampos() {
        txtModelo.setText("");
        comboMarca.setSelectedIndex(-1);
        idSelecionado = 0;
    }
}