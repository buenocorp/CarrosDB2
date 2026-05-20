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
import org.junit.jupiter.api.Timeout;

import view.TelaHome;

public class DemoTelaHomeTest {

    private TelaHome telaHome;

    @BeforeEach
    void setup() throws Exception {
        database.ConnectionFactory.init();

        SwingUtilities.invokeAndWait(() -> {
            telaHome = new TelaHome("Admin");
            telaHome.setVisible(true);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (telaHome != null) telaHome.dispose();
        });
    }

    @Test
    @Timeout(30)
    void abrirSelecionarMarcasDigitarESalvar() throws Exception {

        // 1. Usuário vê o dashboard
        pausa(1500);

        // 2. Clica em "Marcas" no sidebar
        SwingUtilities.invokeAndWait(() ->
            buscarBotaoTexto(telaHome.getContentPane(), "Marcas").doClick()
        );
        pausa(1000);

        // 3. Preenche o campo Nome letra por letra
        List<JTextField> campos = buscarTodosVisiveis(
            telaHome.getContentPane(), JTextField.class
        );

        digitarDevagar(campos.get(0), "Ferrari");
        pausa(400);

        // 4. Preenche o campo País
        digitarDevagar(campos.get(1), "Itália");
        pausa(800);

        // 5. Agenda fechar o JOptionPane "Marca salva!" que vai aparecer
        agendarFechamentoDosDialogos();

        // 6. Clica em Salvar
        SwingUtilities.invokeAndWait(() ->
            buscarBotaoExato(telaHome.getContentPane(), "Salvar").doClick()
        );

        // 7. Usuário vê a tabela atualizada com a nova marca
        pausa(2000);
    }

    // =========================================================
    // Utilitários
    // =========================================================

    // Fecha qualquer JDialog visível após 300ms (para JOptionPane não travar o EDT)
    private void agendarFechamentoDosDialogos() {
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            SwingUtilities.invokeLater(() -> {
                for (Window w : Window.getWindows()) {
                    if (w instanceof JDialog d && d.isVisible()) d.dispose();
                }
            });
        }).start();
    }

    // Digita cada caractere com intervalo, simulando digitação humana
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

    // Busca botão cujo texto (sem espaços) é exatamente igual ao texto pedido
    private static JButton buscarBotaoTexto(Container container, String texto) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && b.getText() != null
                    && b.getText().trim().equals(texto)) return b;
            if (c instanceof Container sub) {
                JButton f = buscarBotaoTexto(sub, texto);
                if (f != null) return f;
            }
        }
        return null;
    }

    // Busca botão com texto exato (sem trim)
    private static JButton buscarBotaoExato(Container container, String texto) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && texto.equals(b.getText())) return b;
            if (c instanceof Container sub) {
                JButton f = buscarBotaoExato(sub, texto);
                if (f != null) return f;
            }
        }
        return null;
    }

    // Busca componentes apenas nos painéis visíveis (ignora cards ocultos do CardLayout)
    private static <T extends Component> List<T> buscarTodosVisiveis(
            Container container, Class<T> tipo) {
        List<T> result = new ArrayList<>();
        for (Component c : container.getComponents()) {
            if (!c.isVisible()) continue;
            if (tipo.isInstance(c)) result.add(tipo.cast(c));
            if (c instanceof Container sub)
                result.addAll(buscarTodosVisiveis(sub, tipo));
        }
        return result;
    }
}
