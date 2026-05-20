package view;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import controller.MarcaController;
import dao.MarcaDAO;
import model.Marca;

public class TelaMarcasListagem extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final Color COR_CONTEUDO  = new Color(245, 245, 248);
    private static final Color COR_BRANCO    = Color.WHITE;
    private static final Color COR_AZUL      = new Color(21, 101, 192);

    // Cores de destaque dos botões de ação
    private static final Color COR_EDITAR  = new Color(56, 142, 60);    // verde
    private static final Color COR_EXCLUIR = new Color(46, 125, 50);    // verde escuro

    private JPanel contentPane;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private MarcaController controller;

    public TelaMarcasListagem(java.awt.Window parent) {

		configurarJanela();
        criarComponentes();
        atualizarTabela();
    }

    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        contentPane = new JPanel(new BorderLayout(0, 15));
        contentPane.setBackground(COR_CONTEUDO);
        contentPane.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(contentPane);
        
		try {
			MarcaDAO marcaDAO = new MarcaDAO();
	         controller =
	                 new MarcaController(marcaDAO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void criarComponentes() {
        // -- Cabeçalho --
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Marcas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 33, 33));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JButton btnNova = criarBotao("+ Nova Marca", COR_AZUL);
        btnNova.addActionListener(e -> abrirFormulario(0, "", ""));
        headerPanel.add(btnNova, BorderLayout.EAST);

        contentPane.add(headerPanel, BorderLayout.NORTH);

        // -- Tabela --
        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "País", "Ações"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(40);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(232, 234, 246));
        tabela.setSelectionBackground(new Color(197, 202, 233));
        tabela.setGridColor(new Color(220, 220, 230));

        // Ocultar coluna ID
        TableColumn colId = tabela.getColumnModel().getColumn(0);
        colId.setMinWidth(0);
        colId.setMaxWidth(0);

        // Coluna Ações
        TableColumn colAcoes = tabela.getColumnModel().getColumn(3);
        colAcoes.setPreferredWidth(210);
        colAcoes.setMinWidth(190);
        colAcoes.setCellRenderer(new AcoesCellRenderer());
        colAcoes.setCellEditor(new AcoesCellEditor());

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        scroll.getViewport().setBackground(COR_BRANCO);
        contentPane.add(scroll, BorderLayout.CENTER);
    }

    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        for (Marca m : controller.listarMarcas()) {
            modeloTabela.addRow(new Object[]{m.getId(), m.getNome(), m.getPais(), null});
        }
    }

    private void abrirFormulario(int id, String nomeAtual, String paisAtual) {
        JTextField txtNome = new JTextField(nomeAtual, 22);
        JTextField txtPais = new JTextField(paisAtual, 22);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.add(new JLabel("Nome:"));
        form.add(txtNome);
        form.add(new JLabel("País:"));
        form.add(txtPais);

        int resultado = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(contentPane),
                form,
                id == 0 ? "Nova Marca" : "Editar Marca",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (resultado != JOptionPane.OK_OPTION) return;

        String nome = txtNome.getText().trim();
        String pais = txtPais.getText().trim();

        if (nome.isEmpty() || pais.isEmpty()) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(contentPane),
                    "Preencha todos os campos!");
            return;
        }

        if (id == 0) {
            controller.salvarMarca(nome, pais);
        } else {
            controller.atualizarMarca(id, nome, pais);
        }

        atualizarTabela();
    }

    private void excluirMarca(int row) {
        int id      = (int)    modeloTabela.getValueAt(row, 0);
        String nome = (String) modeloTabela.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(contentPane),
                "Deseja excluir a marca \"" + nome + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.excluirMarca(id);
            atualizarTabela();
        }
    }

    // Botão genérico (ex: "Nova Marca")
    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(COR_BRANCO);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    // Botão de ação da tabela: cor de destaque + ícone à esquerda
    private JButton criarBotaoAcao(String texto, Color cor, Icon icone) {
        JButton btn = new JButton(texto, icone);
        btn.setBackground(cor);
        btn.setForeground(COR_BRANCO);
        btn.setOpaque(true);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        btn.setIconTextGap(5);
        btn.setHorizontalTextPosition(SwingConstants.RIGHT);
        return btn;
    }

    // =========================================================
    // Ícone de lápis (Editar)
    // =========================================================
    private static class PencilIcon implements Icon {
        private final Color cor;
        PencilIcon(Color cor) { this.cor = cor; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // corpo do lápis (diagonal)
            g2.setColor(cor);
            int[] bx = {x + 3, x + 11, x + 9, x + 1};
            int[] by = {y + 11, y + 3,  y + 1, y + 9};
            g2.fillPolygon(bx, by, 4);

            // ponteira
            g2.setColor(cor.darker());
            g2.fillPolygon(
                new int[]{x + 1, x + 3, x + 0},
                new int[]{y + 9, y + 11, y + 13},
                3);

            // topo (borracha)
            g2.setColor(new Color(255, 180, 60));
            g2.fillRect(x + 10, y + 0, 3, 3);

            g2.dispose();
        }

        @Override public int getIconWidth()  { return 14; }
        @Override public int getIconHeight() { return 14; }
    }

    // =========================================================
    // Ícone de lixeira (Excluir)
    // =========================================================
    private static class TrashIcon implements Icon {
        private final Color cor;
        TrashIcon(Color cor) { this.cor = cor; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // corpo
            g2.setColor(cor);
            g2.fill(new RoundRectangle2D.Float(x + 2, y + 4, 10, 9, 2, 2));

            // tampa
            g2.fillRoundRect(x + 1, y + 2, 12, 3, 1, 1);

            // alça
            g2.setStroke(new BasicStroke(1.4f));
            g2.setColor(cor);
            g2.drawArc(x + 4, y - 1, 6, 4, 0, 180);

            // linhas internas (brancas)
            g2.setColor(COR_BRANCO);
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawLine(x + 5, y + 6, x + 5, y + 11);
            g2.drawLine(x + 7, y + 6, x + 7, y + 11);
            g2.drawLine(x + 9, y + 6, x + 9, y + 11);

            g2.dispose();
        }

        @Override public int getIconWidth()  { return 14; }
        @Override public int getIconHeight() { return 14; }
    }

    // =========================================================
    // Renderer da coluna "Ações" (apenas visual)
    // =========================================================
    private class AcoesCellRenderer extends JPanel implements TableCellRenderer {

        AcoesCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
            add(criarBotaoAcao("Editar",  COR_EDITAR,  new PencilIcon(COR_BRANCO)));
            add(criarBotaoAcao("Excluir", COR_EXCLUIR, new TrashIcon(COR_BRANCO)));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : COR_BRANCO);
            return this;
        }
    }

    // =========================================================
    // Editor da coluna "Ações" (cliques reais)
    // =========================================================
    private class AcoesCellEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        private int currentRow;

        AcoesCellEditor() {
            JButton btnEditar  = criarBotaoAcao("Editar",  COR_EDITAR,  new PencilIcon(COR_BRANCO));
            JButton btnExcluir = criarBotaoAcao("Excluir", COR_EXCLUIR, new TrashIcon(COR_BRANCO));

            panel.add(btnEditar);
            panel.add(btnExcluir);

            btnEditar.addActionListener(e -> {
                fireEditingStopped();
                int id      = (int)    modeloTabela.getValueAt(currentRow, 0);
                String nome = (String) modeloTabela.getValueAt(currentRow, 1);
                String pais = (String) modeloTabela.getValueAt(currentRow, 2);
                abrirFormulario(id, nome, pais);
            });

            btnExcluir.addActionListener(e -> {
                fireEditingStopped();
                excluirMarca(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }
}
