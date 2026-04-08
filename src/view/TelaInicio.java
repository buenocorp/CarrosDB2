package view;

import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import model.ValidadorDocumento;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;

import java.awt.event.ActionListener;
import java.text.ParseException;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class TelaInicio extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private JFormattedTextField txtCpf;
	private JFormattedTextField txtCnpj;

	public static void main(String[] args) {

		database.ConnectionFactory.init();
		EventQueue.invokeLater(() -> {
			try {
				TelaInicio frame = new TelaInicio();
				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public TelaInicio() {

		setTitle("Sistema de Cadastro");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		setBounds(100, 100, 450, 300);
		setExtendedState(JFrame.MAXIMIZED_BOTH); // maximiza a janela
		setLocationRelativeTo(null);

		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnMarca = new JButton("Cadastrar Marca");
		btnMarca.setBounds(10, 42, 165, 30);
		btnMarca.setForeground(Color.RED);
		contentPane.add(btnMarca);

		btnMarca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TelaMarca tela = new TelaMarca(null);
				tela.setVisible(true);
			}
		});

		ImageIcon icon = new ImageIcon("/Users/fabiobueno/Downloads/migration-v5-v6.png");
		Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
		icon = new ImageIcon(img);

		JButton btnNewButton = new JButton("Validar CPF", icon);
		btnNewButton.setBounds(231, 112, 114, 65);
		btnNewButton.setHorizontalTextPosition(JButton.CENTER);
		btnNewButton.setVerticalTextPosition(JButton.BOTTOM);

		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String cpf = txtCpf.getText();
				String cnpj = txtCnpj.getText();

				if (!ValidadorDocumento.isCPFValido(cpf)) {
					JOptionPane.showMessageDialog(null, "CPF inválido!");
					return;
				}

//	    		if (!ValidadorDocumento.isCNPJValido(cnpj)) {
//	    		    JOptionPane.showMessageDialog(null, "CNPJ inválido!");
//	    		    return;
//	    		}
			}
		});

		try {
			// CPF: 000.000.000-00
			MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
			cpfMask.setPlaceholderCharacter('_');

			// CNPJ: 00.000.000/0000-00
			MaskFormatter cnpjMask = new MaskFormatter("##.###.###/####-##");
			cnpjMask.setPlaceholderCharacter('_');

			txtCpf = new JFormattedTextField(cpfMask);
			txtCpf.setBounds(231, 43, 173, 26);
			contentPane.add(txtCpf);

			txtCnpj = new JFormattedTextField(cnpjMask);
			txtCnpj.setBounds(231, 79, 173, 26);
			contentPane.add(txtCnpj);

			// Pegar valores sem máscara - para salvar no banco
//		    String cpfLimpo = txtCpf.getText().replaceAll("[^0-9]", "");
//		    String cnpjLimpo = txtCnpj.getText().replaceAll("[^0-9]", "");

		} catch (ParseException e) {
			e.printStackTrace();
		}

		JButton btnModelo_1 = new JButton("Cadastrar Modelo");
		btnModelo_1.setBounds(10, 148, 165, 29);
		btnModelo_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TelaModelo2 tela = new TelaModelo2(null);
				tela.setVisible(true);
			}
		});

		JButton btnModelo = new JButton("Cadastrar Modelo");
		btnModelo.setBounds(10, 77, 165, 30);
		btnModelo.setForeground(Color.RED);
		contentPane.add(btnModelo);

		btnModelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TelaModelo tela = new TelaModelo(null);
				tela.setVisible(true);
			}
		});

		btnModelo_1.setForeground(Color.RED);
		contentPane.add(btnModelo_1);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Popup");
		btnNewButton_1.setBounds(51, 213, 83, 29);
		contentPane.add(btnNewButton_1);

		// Carregar imagem do projeto
//		ImageIcon icon = new ImageIcon(
//			    getClass().getResource("/images/save.png"));

		JPopupMenu menu = new JPopupMenu();
		JMenuItem item1 = new JMenuItem("Opção 1");
		JMenuItem item2 = new JMenuItem("Opção 2");

		menu.add(item1);
		menu.add(item2);

		// ação
		item1.addActionListener(e -> {
			System.out.println("Clicou na opção 1");
		});

		item2.addActionListener(e -> {
			System.out.println("Clicou na opção 2");
		});

		// =====================
		// Barra de menu
		// =====================
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		// ---------------------
		// Menu Cadastro
		// ---------------------
		JMenu menuCadastro = new JMenu("Cadastro");
		menuBar.add(menuCadastro);

		JMenuItem itemMarca = new JMenuItem("Marca");
		JMenuItem itemModelo = new JMenuItem("Modelo");
		JMenuItem itemUsuario = new JMenuItem("Usuário");

		menuCadastro.add(itemMarca);
		// Separador
		menuCadastro.addSeparator();
		menuCadastro.add(itemModelo);
		// Separador
		menuCadastro.addSeparator();
		menuCadastro.add(itemUsuario);

		// =====================
		// Ações dos itens
		// =====================
		itemMarca.addActionListener(e -> {
			TelaMarca tela = new TelaMarca(null);
			tela.setVisible(true);

		});
		itemModelo.addActionListener(e -> {
			TelaMarca tela = new TelaMarca(null);
			tela.setVisible(true);
		});
		itemUsuario.addActionListener(e -> {
			TelaMarca tela = new TelaMarca(null);
			tela.setVisible(true);
		});

		// ---------------------
		// Menu Sistema
		// ---------------------
		JMenu menuSistema = new JMenu("Sistema");
		menuBar.add(menuSistema);

		JMenuItem itemSair = new JMenuItem("Sair");
		JMenuItem itemMarca2 = new JMenuItem("Marca");
		JMenuItem itemModelo2 = new JMenuItem("Modelo");
		JMenuItem itemUsuario2 = new JMenuItem("Usuário");

		menuSistema.add(itemMarca2);
		menuSistema.add(itemModelo2);
		menuSistema.add(itemUsuario2);
		menuSistema.add(itemSair);

		itemSair.addActionListener(e -> System.exit(0));
		
		btnNewButton_1.addActionListener(e -> {
			menu.show(btnNewButton_1, 10, -menu.getPreferredSize().height);
		});


	}
}