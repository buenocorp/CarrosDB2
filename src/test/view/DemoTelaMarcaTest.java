package test.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import view.TelaInicio;
import view.TelaMarca;

public class DemoTelaMarcaTest {

    private TelaInicio telaInicio;

    @BeforeEach
    void setup() throws Exception {
        database.ConnectionFactory.init();

        SwingUtilities.invokeAndWait(() -> {
            telaInicio = new TelaInicio();
            telaInicio.setSize(500, 300);
            telaInicio.setLocationRelativeTo(null);
            telaInicio.setVisible(true);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (telaInicio != null) telaInicio.dispose();
        });
    }

    @Test
    void abrirDigitarESalvarMarca() throws Exception {

        pausa(1500); // usuário olha a TelaInicio

        // Thread paralela: vai interagir com TelaMarca quando ela aparecer.
        // Necessário porque TelaMarca é modal e bloqueia o EDT enquanto está aberta.
        Thread interacao = new Thread(() -> {
            try {
                TelaMarca tela = esperarJanela(TelaMarca.class);
                pausa(800);

                List<JTextField> campos =
                    buscarTodos(tela.getContentPane(), JTextField.class);

                digitarDevagar(campos.get(0), "Ferrari");
                pausa(500);
                digitarDevagar(campos.get(1), "Itália");
                pausa(1000);

                // clica Salvar
                SwingUtilities.invokeAndWait(() ->
                    buscarBotao(tela.getContentPane(), "Salvar").doClick()
                );
                pausa(1500); // usuário lê o "Marca salva!"

                // fecha o JOptionPane
                SwingUtilities.invokeAndWait(() -> {
                    for (Window w : Window.getWindows()) {
                        if (w instanceof JDialog d && d != tela && d.isVisible())
                            d.dispose();
                    }
                });
                pausa(1500); // usuário vê a tabela atualizada

                SwingUtilities.invokeAndWait(() -> tela.dispose());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        interacao.start();

        // Clica em "Cadastrar Marca" na TelaInicio.
        // Como TelaMarca é modal, este invokeAndWait só retorna quando TelaMarca fechar.
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaInicio.getContentPane(), "Cadastrar Marca").doClick()
        );

        interacao.join(15_000);

        pausa(1000); // usuário vê TelaInicio de volta
    }

    // Fica tentando encontrar uma janela do tipo pedido até aparecer
    @SuppressWarnings("unchecked")
    private <T extends Window> T esperarJanela(Class<T> tipo)
            throws InterruptedException {
        for (int i = 0; i < 100; i++) {
            for (Window w : Window.getWindows()) {
                if (tipo.isInstance(w) && w.isVisible())
                    return (T) w;
            }
            Thread.sleep(100);
        }
        throw new RuntimeException("Janela não apareceu: " + tipo.getSimpleName());
    }

    private void digitarDevagar(JTextField campo, String texto) throws Exception {
        for (char c : texto.toCharArray()) {
            SwingUtilities.invokeAndWait(() ->
                campo.setText(campo.getText() + c)
            );
            Thread.sleep(130);
        }
    }

    private static void pausa(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    private static JButton buscarBotao(Container container, String texto) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && texto.equals(b.getText())) return b;
            if (c instanceof Container sub) {
                JButton f = buscarBotao(sub, texto);
                if (f != null) return f;
            }
        }
        return null;
    }

    private static <T extends Component> List<T> buscarTodos(Container container, Class<T> tipo) {
        List<T> result = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (tipo.isInstance(c)) result.add(tipo.cast(c));
            if (c instanceof Container sub) result.addAll(buscarTodos(sub, tipo));
        }
        return result;
    }
}
