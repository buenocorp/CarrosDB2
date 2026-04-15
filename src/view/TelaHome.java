package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;


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
    private JButton btnInicio;
    private JButton btnMarcas;
    private JButton btnMarcas2;
    private JButton btnModelos;

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
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(criarTopBar(), BorderLayout.NORTH);
        getContentPane().add(criarSidebar(), BorderLayout.WEST);
        getContentPane().add(criarAreaConteudo(), BorderLayout.CENTER);
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

        btnInicio  = new JButton("  Início");
        btnMarcas  = new JButton("  Marcas");
        btnMarcas2  = new JButton("  Marcas 2");
        aplicarEstiloBotaoMenu(btnInicio,  "INICIO");
        aplicarEstiloBotaoMenu(btnMarcas,  "MARCAS");
        aplicarEstiloBotaoMenu(btnMarcas2,  "MARCAS 2");

        sidebar.add(btnInicio);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnMarcas);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(btnMarcas2);
        sidebar.add(Box.createVerticalStrut(2));
        btnModelos = new JButton("  Modelos");
        aplicarEstiloBotaoMenu(btnModelos, "MODELOS");
        sidebar.add(btnModelos);

        sidebar.add(Box.createVerticalGlue());

        marcarAtivo(btnInicio);
        return sidebar;
    }

    private void aplicarEstiloBotaoMenu(JButton btn, String card) {
//        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
//        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
//        btn.setBackground(COR_SIDEBAR);
//        btn.setForeground(COR_BRANCO);
//        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
//        btn.setBorderPainted(false);
//        btn.setFocusPainted(false);
//        btn.setHorizontalAlignment(SwingConstants.LEFT);
//        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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
        painelConteudo.add(criarPainelMarca2(),     "MARCAS 2");
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
    // Painel: Listagem de Marcas
    // =========================================================
    private JPanel criarPainelMarca2() {
        TelaMarcasListagem tela = new TelaMarcasListagem(this);
        return (JPanel) tela.getContentPane();
    }
    
 // =========================================================
    // Painel: Listagem de Marcas
    // =========================================================
    private JPanel criarPainelMarca() {
        TelaMarca tela = new TelaMarca(this);
        return (JPanel) tela.getContentPane();
    }

    // =========================================================
    // Painel: CRUD de Modelos
    // =========================================================
    private JPanel criarPainelModelo() {
        TelaModelo telaModelo = new TelaModelo(this);
        return (JPanel) telaModelo.getContentPane();
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
