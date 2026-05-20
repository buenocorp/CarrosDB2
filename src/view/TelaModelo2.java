package view;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.ModeloController;
import dao.MarcaDAO;
import dao.ModeloDAO;
import model.Marca;
import model.Modelo;

public class TelaModelo2 extends JDialog {

    private JPanel contentPane;

    private JTextField txtModelo;
    private JTextField txtMarca;

    private JButton btnBuscarMarca;

    private JTable tabelaModelos;

    private DefaultTableModel modeloTabela;

    private ModeloController controller;

    private int idSelecionado = 0;

    private Marca marcaSelecionada;

    public TelaModelo2(JFrame parent) {

        super(parent, true);

        ModeloDAO modeloDAO = new ModeloDAO();

        MarcaDAO marcaDAO = null;
		try {
			marcaDAO = new MarcaDAO();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        controller =
            new ModeloController(
                modeloDAO,
                marcaDAO
            );

        configurarJanela();

        criarComponentes();

        atualizarTabela();
    }

    private void configurarJanela() {

        setTitle("Cadastro de Modelo");

        setDefaultCloseOperation(
            DISPOSE_ON_CLOSE
        );

        setBounds(100, 100, 520, 420);

        setLocationRelativeTo(null);

        contentPane = new JPanel();

        contentPane.setBorder(
            new EmptyBorder(10, 10, 10, 10)
        );

        contentPane.setLayout(null);

        setContentPane(contentPane);
    }

    private void criarComponentes() {

        JLabel lblModelo =
            new JLabel("Modelo:");

        lblModelo.setBounds(30, 30, 80, 20);

        contentPane.add(lblModelo);

        txtModelo = new JTextField();

        txtModelo.setBounds(120, 30, 218, 25);

        contentPane.add(txtModelo);

        JLabel lblMarca =
            new JLabel("Marca:");

        lblMarca.setBounds(30, 80, 80, 20);

        contentPane.add(lblMarca);

        txtMarca = new JTextField();

        txtMarca.setBounds(120, 80, 140, 25);

        txtMarca.setEditable(false);

        contentPane.add(txtMarca);

        btnBuscarMarca =
            new JButton("Buscar");

        btnBuscarMarca.setBounds(
            272,
            79,
            72,
            25
        );

        contentPane.add(btnBuscarMarca);

        btnBuscarMarca.addActionListener(
            e -> abrirTelaMarca()
        );

        JButton btnSalvar =
            new JButton("Salvar");

        btnSalvar.setBounds(350, 30, 120, 30);

        contentPane.add(btnSalvar);

        btnSalvar.addActionListener(
            e -> salvarModelo()
        );

        JButton btnEditar =
            new JButton("Editar");

        btnEditar.setBounds(350, 70, 120, 30);

        contentPane.add(btnEditar);

        btnEditar.addActionListener(
            e -> carregarParaEdicao()
        );

        JButton btnExcluir =
            new JButton("Excluir");

        btnExcluir.setBounds(
            350,
            110,
            120,
            30
        );

        contentPane.add(btnExcluir);

        btnExcluir.addActionListener(
            e -> excluirModelo()
        );

        modeloTabela =
            new DefaultTableModel() {

                private static final long serialVersionUID = 1L;

                @Override
                public boolean isCellEditable(
                    int row,
                    int column
                ) {
                    return false;
                }
            };

        modeloTabela.addColumn("ID");

        modeloTabela.addColumn("Modelo");

        modeloTabela.addColumn("Marca");

        tabelaModelos =
            new JTable(modeloTabela);

        tabelaModelos
            .getColumnModel()
            .getColumn(0)
            .setMinWidth(0);

        tabelaModelos
            .getColumnModel()
            .getColumn(0)
            .setMaxWidth(0);

        JScrollPane scroll =
            new JScrollPane(tabelaModelos);

        scroll.setBounds(
            30,
            160,
            440,
            180
        );

        contentPane.add(scroll);
    }

    private void abrirTelaMarca() {

        Marca marca =
            TelaMarca.selecionarMarca(this);

        if (marca != null) {

            marcaSelecionada = marca;

            txtMarca.setText(
                marca.getNome()
            );
        }
    }

    private void salvarModelo() {

        String nomeModelo =
            txtModelo.getText().trim();

        if (
            nomeModelo.isEmpty()
            || marcaSelecionada == null
        ) {

            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(
                    contentPane
                ),
                "Preencha todos os campos!"
            );

            return;
        }

        if (idSelecionado == 0) {

            controller.salvarModelo(
                nomeModelo,
                marcaSelecionada
            );

            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(
                    contentPane
                ),
                "Modelo salvo!"
            );

        } else {

            controller.atualizarModelo(
                idSelecionado,
                nomeModelo,
                marcaSelecionada
            );

            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(
                    contentPane
                ),
                "Modelo atualizado!"
            );

            idSelecionado = 0;
        }

        limparCampos();

        atualizarTabela();
    }

    private void carregarParaEdicao() {

        int linha =
            tabelaModelos.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(
                    contentPane
                ),
                "Selecione um modelo!"
            );

            return;
        }

        idSelecionado =
            (int)
            tabelaModelos.getValueAt(
                linha,
                0
            );

        Modelo modelo =
            controller.buscarModelo(
                idSelecionado
            );

        txtModelo.setText(
            modelo.getNome()
        );

        marcaSelecionada =
            modelo.getMarca();

        txtMarca.setText(
            marcaSelecionada.getNome()
        );
    }

    private void excluirModelo() {

        int linha =
            tabelaModelos.getSelectedRow();

        if (linha == -1) {

            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(
                    contentPane
                ),
                "Selecione um modelo!"
            );

            return;
        }

        int id =
            (int)
            tabelaModelos.getValueAt(
                linha,
                0
            );

        controller.excluirModelo(id);

        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(
                contentPane
            ),
            "Modelo excluído!"
        );

        atualizarTabela();
    }

    private void atualizarTabela() {

        modeloTabela.setRowCount(0);

        for (
            Modelo modelo
                : controller.listarModelos()
        ) {

            modeloTabela.addRow(
                new Object[] {
                    modelo.getId(),
                    modelo.getNome(),
                    modelo.getMarca().getNome()
                }
            );
        }
    }

    private void limparCampos() {

        txtModelo.setText("");

        txtMarca.setText("");

        marcaSelecionada = null;

        idSelecionado = 0;
    }
}