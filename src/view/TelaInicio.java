package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.JScrollPane;

public class TelaInicio extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

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
		contentPane.setBorder(new EmptyBorder(10,10,10,10));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnMarca = new JButton("Cadastrar Marca");
		btnMarca.setBounds(16, 32, 170, 35);
		btnMarca.setForeground(Color.RED);
		contentPane.add(btnMarca);

		JButton btnModelo = new JButton("Cadastrar Modelo");
		btnModelo.setBounds(16, 79, 170, 35);
		btnModelo.setForeground(Color.RED);
		contentPane.add(btnModelo);
		
		JButton btnModelo_1 = new JButton("Cadastrar Modelo");
		btnModelo_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				TelaModelo2 tela = new TelaModelo2(null);
				tela.setVisible(true);
			}
		});
		btnModelo_1.setForeground(Color.RED);
		btnModelo_1.setBounds(16, 147, 170, 35);
		contentPane.add(btnModelo_1);

		btnMarca.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TelaMarca tela = new TelaMarca(null);
				tela.setVisible(true);
			}
		});

		btnModelo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				TelaModelo tela = new TelaModelo(null);
				tela.setVisible(true);
			}
		});
	}
}