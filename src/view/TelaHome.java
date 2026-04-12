package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import controller.MarcaController;
import controller.ModeloController;
import model.Marca;
import model.Modelo;

public class TelaHome extends JFrame {

    private static final long serialVersionUID = 1L;

    // Paleta de cores
    private static final Color COR_TOPBAR      = new Color(26, 35, 126);
    private static final Color COR_SIDEBAR      = new Color(40, 53, 147);
    private static final Color COR_MENU_HOVER   = new Color(48, 63, 159);
    private static final Color COR_MENU_ATIVO   = new Color(21, 101, 192);
    private static final Color COR_CONTEUDO     = new Color(245, 245, 248);
    private static final Color COR_BRANCO       = Color.WHITE;
    private static final Color COR_AZUL         = new Color(21, 101, 192);
    private static final Color COR_VERMELHO     = new Color(183, 28, 28);
    private static final Color COR_CINZA        = new Color(69, 90, 100);

    private final String nomeUsuario;
    private JPanel painelConteudo;
    private CardLayout cardLayout;
    private JButton botaoAtivo;

    public TelaHome(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
        configurarJanela();
        construirLayout();
    }

    // =========================================================
    // Configuração da janela principal
    // =========================================================
    private void configurarJanela() {
        setTitle("Sistema de Cadastro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
    }

    private void construirLayout() {
        setLayout(new BorderLayout());
        add(criarTopBar(), BorderLayout.NORTH);
        add(criarSidebar(), BorderLayout.WEST);
        add(criarAreaConteudo(), BorderLayout.CENTER);
    }

    // =========================================================
    // Barra superior (topo)
    // =========================================================
    private JPanel criarTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COR_TOPBAR);
        topBar.setPreferredSize(new Dimension(0, 56));
        topBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Título à esquerda
        JLabel lblTitulo = new JLabel("Sistema de Cadastro");
        lblTitulo.setForeground(COR_BRANCO);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topBar.add(lblTitulo, BorderLayout.WEST);

        // Nome do usuário + botão Sair à direita
        JPanel painelDireito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        painelDireito.setOpaque(false);

        JLabel lblUsuario = new JLabel("Olá, " + nomeUsuario);
        lblUsuario.setForeground(COR_BRANCO);
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        painelDireito.add(lblUsuario);

        JButton btnSair = new JButton("Sair");
        btnSair.setBackground(COR_TOPBAR);
        btnSair.setForeground(COR_BRANCO);
        btnSair.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSair.setFocusPainted(false);
        btnSair.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> System.exit(0));
        painelDireito.add(btnSair);

        topBar.add(painelDireito, BorderLayout.EAST);
        return topBar;
    }

    // =========================================================
    // Sidebar (menu lateral esquerdo)
    // =========================================================
    private JPanel criarSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(COR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(15, 0, 15, 0));

        String[][] itens = {
            { "  Início",   "INICIO"  },
            { "  Marcas",   "MARCAS"  },
            { "  Modelos",  "MODELOS" },
        };

        for (String[] item : itens) {
            JButton btn = criarBotaoMenu(item[0], item[1]);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));

            // O primeiro botão começa ativo
            if (item[1].equals("INICIO")) {
                marcarAtivo(btn);
            }
        }

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton criarBotaoMenu(String texto, String card) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBackground(COR_SIDEBAR);
        btn.setForeground(COR_BRANCO);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != botaoAtivo) btn.setBackground(COR_MENU_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != botaoAtivo) btn.setBackground(COR_SIDEBAR);
            }
        });

        btn.addActionListener(e -> {
            marcarAtivo(btn);
            cardLayout.show(painelConteudo, card);
        });

        return btn;
    }

    private void marcarAtivo(JButton btn) {
        if (botaoAtivo != null) {
            botaoAtivo.setBackground(COR_SIDEBAR);
        }
        botaoAtivo = btn;
        btn.setBackground(COR_MENU_ATIVO);
    }

    // =========================================================
    // Área de conteúdo central (CardLayout)
    // =========================================================
    private JPanel criarAreaConteudo() {
        cardLayout = new CardLayout();
        painelConteudo = new JPanel(cardLayout);
        painelConteudo.setBackground(COR_CONTEUDO);

        painelConteudo.add(criarPainelDashboard(), "INICIO");
        painelConteudo.add(criarPainelMarca(),     "MARCAS");
        painelConteudo.add(criarPainelModelo(),    "MODELOS");

        cardLayout.show(painelConteudo, "INICIO");
        return painelConteudo;
    }

    // =========================================================
    // Painel: Dashboard / Início
    // =========================================================
    private JPanel criarPainelDashboard() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(COR_CONTEUDO);

        JPanel caixa = new JPanel();
        caixa.setBackground(COR_BRANCO);
        caixa.setLayout(new BoxLayout(caixa, BoxLayout.Y_AXIS));
        caixa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(40, 50, 40, 50)
        ));

        JLabel lblBemVindo = new JLabel("Bem-vindo, " + nomeUsuario + "!");
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBemVindo.setForeground(new Color(33, 33, 33));
        lblBemVindo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Utilize o menu lateral para navegar pelo sistema.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblSub.setForeground(new Color(100, 100, 100));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        caixa.add(lblBemVindo);
        caixa.add(Box.createVerticalStrut(12));
        caixa.add(lblSub);

        painel.add(caixa);
        return painel;
    }

    // =========================================================
    // Painel: CRUD de Marcas
    // =========================================================
    private JPanel criarPainelMarca() {
        JPanel painel = new JPanel(null);
        painel.setBackground(COR_CONTEUDO);

        MarcaController controller = new MarcaController();
        int[] idSelecionado = { 0 };

        // --- Título ---
        JLabel lblTitulo = new JLabel("Cadastro de Marcas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 33, 33));
        lblTitulo.setBounds(30, 25, 350, 35);
        painel.add(lblTitulo);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 65, 700, 2);
        painel.add(sep);

        // --- Formulário ---
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(30, 85, 80, 25);
        painel.add(lblNome);

        JTextField txtNome = new JTextField();
        txtNome.setBounds(110, 85, 230, 30);
        painel.add(txtNome);

        JLabel lblPais = new JLabel("País:");
        lblPais.setBounds(30, 130, 80, 25);
        painel.add(lblPais);

        JTextField txtPais = new JTextField();
        txtPais.setBounds(110, 130, 230, 30);
        painel.add(txtPais);

        // --- Botões ---
        JButton btnSalvar  = new JButton("Salvar");
        JButton btnEditar  = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar  = new JButton("Limpar");

        btnSalvar .setBounds(360, 85,  120, 32);
        btnEditar .setBounds(360, 125, 120, 32);
        btnExcluir.setBounds(490, 85,  120, 32);
        btnLimpar .setBounds(490, 125, 120, 32);

        estilizarBotao(btnSalvar,  COR_AZUL);
        estilizarBotao(btnEditar,  COR_AZUL);
        estilizarBotao(btnExcluir, COR_VERMELHO);
        estilizarBotao(btnLimpar,  COR_CINZA);

        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnExcluir);
        painel.add(btnLimpar);

        // --- Tabela ---
        DefaultTableModel modeloTabela = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("País");

        JTable tabela = estilizarTabela(new JTable(modeloTabela));
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(30, 180, 700, 320);
        painel.add(scroll);

        // --- Lógica ---
        Runnable atualizar = () -> {
            modeloTabela.setRowCount(0);
            for (Marca m : controller.listarMarcas()) {
                modeloTabela.addRow(new Object[]{ m.getId(), m.getNome(), m.getPais() });
            }
        };

        Runnable limpar = () -> {
            txtNome.setText("");
            txtPais.setText("");
            idSelecionado[0] = 0;
        };

        atualizar.run();

        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            String pais = txtPais.getText().trim();
            if (nome.isEmpty() || pais.isEmpty()) {
                JOptionPane.showMessageDialog(painel, "Preencha todos os campos!");
                return;
            }
            if (idSelecionado[0] == 0) {
                controller.salvarMarca(nome, pais);
                JOptionPane.showMessageDialog(painel, "Marca salva com sucesso!");
            } else {
                controller.atualizarMarca(idSelecionado[0], nome, pais);
                JOptionPane.showMessageDialog(painel, "Marca atualizada com sucesso!");
            }
            limpar.run();
            atualizar.run();
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(painel, "Selecione uma marca!"); return; }
            idSelecionado[0] = (int) tabela.getValueAt(linha, 0);
            Marca m = controller.buscarMarca(idSelecionado[0]);
            txtNome.setText(m.getNome());
            txtPais.setText(m.getPais());
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(painel, "Selecione uma marca!"); return; }
            int ok = JOptionPane.showConfirmDialog(painel, "Confirmar exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.excluirMarca((int) tabela.getValueAt(linha, 0));
                atualizar.run();
            }
        });

        btnLimpar.addActionListener(e -> limpar.run());

        painel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) { atualizar.run(); }
        });

        return painel;
    }

    // =========================================================
    // Painel: CRUD de Modelos
    // =========================================================
    private JPanel criarPainelModelo() {
        JPanel painel = new JPanel(null);
        painel.setBackground(COR_CONTEUDO);

        ModeloController controller = new ModeloController();
        int[] idSelecionado = { 0 };

        // --- Título ---
        JLabel lblTitulo = new JLabel("Cadastro de Modelos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(33, 33, 33));
        lblTitulo.setBounds(30, 25, 350, 35);
        painel.add(lblTitulo);

        JSeparator sep = new JSeparator();
        sep.setBounds(30, 65, 700, 2);
        painel.add(sep);

        // --- Formulário ---
        JLabel lblNome = new JLabel("Nome:");
        lblNome.setBounds(30, 85, 80, 25);
        painel.add(lblNome);

        JTextField txtNome = new JTextField();
        txtNome.setBounds(110, 85, 230, 30);
        painel.add(txtNome);

        JLabel lblMarca = new JLabel("Marca:");
        lblMarca.setBounds(30, 130, 80, 25);
        painel.add(lblMarca);

        JComboBox<Marca> cmbMarca = new JComboBox<>();
        cmbMarca.setBounds(110, 130, 230, 30);
        painel.add(cmbMarca);

        // --- Botões ---
        JButton btnSalvar  = new JButton("Salvar");
        JButton btnEditar  = new JButton("Editar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar  = new JButton("Limpar");

        btnSalvar .setBounds(360, 85,  120, 32);
        btnEditar .setBounds(360, 125, 120, 32);
        btnExcluir.setBounds(490, 85,  120, 32);
        btnLimpar .setBounds(490, 125, 120, 32);

        estilizarBotao(btnSalvar,  COR_AZUL);
        estilizarBotao(btnEditar,  COR_AZUL);
        estilizarBotao(btnExcluir, COR_VERMELHO);
        estilizarBotao(btnLimpar,  COR_CINZA);

        painel.add(btnSalvar);
        painel.add(btnEditar);
        painel.add(btnExcluir);
        painel.add(btnLimpar);

        // --- Tabela ---
        DefaultTableModel modeloTabela = new DefaultTableModel() {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        modeloTabela.addColumn("ID");
        modeloTabela.addColumn("Nome");
        modeloTabela.addColumn("Marca");

        JTable tabela = estilizarTabela(new JTable(modeloTabela));
        tabela.getColumnModel().getColumn(0).setMinWidth(0);
        tabela.getColumnModel().getColumn(0).setMaxWidth(0);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBounds(30, 180, 700, 320);
        painel.add(scroll);

        // --- Lógica ---
        Runnable carregarMarcas = () -> {
            Object selecionada = cmbMarca.getSelectedItem();
            cmbMarca.removeAllItems();
            for (Marca m : controller.listarMarcas()) {
                cmbMarca.addItem(m);
            }
            if (selecionada != null) cmbMarca.setSelectedItem(selecionada);
        };

        Runnable atualizar = () -> {
            modeloTabela.setRowCount(0);
            for (Modelo m : controller.listarModelos()) {
                String nomeMarca = (m.getMarca() != null) ? m.getMarca().getNome() : "-";
                modeloTabela.addRow(new Object[]{ m.getId(), m.getNome(), nomeMarca });
            }
        };

        Runnable limpar = () -> {
            txtNome.setText("");
            idSelecionado[0] = 0;
            if (cmbMarca.getItemCount() > 0) cmbMarca.setSelectedIndex(0);
        };

        carregarMarcas.run();
        atualizar.run();

        btnSalvar.addActionListener(e -> {
            String nome = txtNome.getText().trim();
            Marca marca = (Marca) cmbMarca.getSelectedItem();
            if (nome.isEmpty() || marca == null) {
                JOptionPane.showMessageDialog(painel, "Preencha todos os campos!");
                return;
            }
            if (idSelecionado[0] == 0) {
                controller.salvarModelo(nome, marca);
                JOptionPane.showMessageDialog(painel, "Modelo salvo com sucesso!");
            } else {
                controller.atualizarModelo(idSelecionado[0], nome, marca);
                JOptionPane.showMessageDialog(painel, "Modelo atualizado com sucesso!");
            }
            limpar.run();
            atualizar.run();
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(painel, "Selecione um modelo!"); return; }
            idSelecionado[0] = (int) tabela.getValueAt(linha, 0);
            Modelo m = controller.buscarModelo(idSelecionado[0]);
            txtNome.setText(m.getNome());
            if (m.getMarca() != null) {
                for (int i = 0; i < cmbMarca.getItemCount(); i++) {
                    if (cmbMarca.getItemAt(i).getId() == m.getMarca().getId()) {
                        cmbMarca.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha == -1) { JOptionPane.showMessageDialog(painel, "Selecione um modelo!"); return; }
            int ok = JOptionPane.showConfirmDialog(painel, "Confirmar exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                controller.excluirModelo((int) tabela.getValueAt(linha, 0));
                atualizar.run();
            }
        });

        btnLimpar.addActionListener(e -> limpar.run());

        painel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                carregarMarcas.run();
                atualizar.run();
            }
        });

        return painel;
    }

    // =========================================================
    // Utilitários de estilo
    // =========================================================
    private void estilizarBotao(JButton btn, Color cor) {
        btn.setBackground(cor);
        btn.setForeground(COR_AZUL);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JTable estilizarTabela(JTable tabela) {
        tabela.setRowHeight(28);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(232, 234, 246));
        tabela.setSelectionBackground(new Color(197, 202, 233));
        tabela.setGridColor(new Color(220, 220, 230));
        return tabela;
    }

    // =========================================================
    // Ponto de entrada (para testes isolados)
    // =========================================================
    public static void main(String[] args) {
        database.ConnectionFactory.init();
        EventQueue.invokeLater(() -> {
            TelaHome tela = new TelaHome("Admin");
            tela.setVisible(true);
        });
    }
}
