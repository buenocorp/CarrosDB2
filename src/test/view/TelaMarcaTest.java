package test.view;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import controller.MarcaController;
import model.Marca;
import view.TelaInicio;
import view.TelaMarca;

public class TelaMarcaTest {

    private MarcaController controllerMock;
    private TelaMarca telaMarca;
    private TelaInicio telaInicio;

    @BeforeEach
    void setup() throws Exception {
        controllerMock = mock(MarcaController.class);
        when(controllerMock.listarMarcas()).thenReturn(new ArrayList<>());

        SwingUtilities.invokeAndWait(() -> {
            telaInicio = new TelaInicio();
            telaMarca = new TelaMarca(null, controllerMock);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            if (telaMarca != null && telaMarca.isDisplayable()) telaMarca.dispose();
            if (telaInicio != null) telaInicio.dispose();
        });
    }

    // =============================================
    // TelaInicio → TelaMarca
    // =============================================

    @Test
    void telaInicioTemBotaoCadastrarMarca() {
        JButton btn = buscarBotao(telaInicio.getContentPane(), "Cadastrar Marca");
        assertNotNull(btn, "TelaInicio deve ter o botão 'Cadastrar Marca'");
    }

    // =============================================
    // TelaMarca — estado inicial
    // =============================================

    @Test
    void telaMarcaAbreComTituloCorreto() {
        assertEquals("Cadastro de Marca", telaMarca.getTitle());
    }

    @Test
    void telaMarcaTemDoisCamposDeTexto() {
        List<JTextField> campos = buscarTodos(telaMarca.getContentPane(), JTextField.class);
        assertEquals(2, campos.size(), "Deve ter campos Nome e País");
    }

    @Test
    void tabelaCarregaMarcasDoController() throws Exception {
        List<Marca> marcas = Arrays.asList(
            marcaCom(1, "Toyota", "Japão"),
            marcaCom(2, "BMW", "Alemanha")
        );
        when(controllerMock.listarMarcas()).thenReturn(marcas);

        SwingUtilities.invokeAndWait(() -> {
            telaMarca.dispose();
            telaMarca = new TelaMarca(null, controllerMock);
        });

        JTable tabela = buscarTodos(telaMarca.getContentPane(), JTable.class).get(0);

        assertEquals(2, tabela.getRowCount());
        assertEquals("Toyota", tabela.getModel().getValueAt(0, 1));
        assertEquals("BMW",    tabela.getModel().getValueAt(1, 1));
    }

    // =============================================
    // TelaMarca — salvar
    // =============================================

    @Test
    void salvarMarcaChamaController() throws Exception {
        List<JTextField> campos = buscarTodos(telaMarca.getContentPane(), JTextField.class);

        SwingUtilities.invokeAndWait(() -> {
            campos.get(0).setText("Toyota");
            campos.get(1).setText("Japão");
        });

        agendarFechamentoDosDialogos();
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaMarca.getContentPane(), "Salvar").doClick()
        );

        verify(controllerMock).salvarMarca("Toyota", "Japão");
    }

    @Test
    void salvarComCamposVaziosNaoChamaController() throws Exception {
        agendarFechamentoDosDialogos();
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaMarca.getContentPane(), "Salvar").doClick()
        );

        verify(controllerMock, never()).salvarMarca(any(), any());
    }

    // =============================================
    // TelaMarca — editar
    // =============================================

    @Test
    void editarCarregaDadosNosCampos() throws Exception {
        Marca marca = marcaCom(1, "BMW", "Alemanha");
        when(controllerMock.listarMarcas()).thenReturn(List.of(marca));
        when(controllerMock.buscarMarca(1)).thenReturn(marca);

        SwingUtilities.invokeAndWait(() -> {
            telaMarca.dispose();
            telaMarca = new TelaMarca(null, controllerMock);
        });

        SwingUtilities.invokeAndWait(() -> {
            JTable tabela = buscarTodos(telaMarca.getContentPane(), JTable.class).get(0);
            tabela.setRowSelectionInterval(0, 0);
            buscarBotao(telaMarca.getContentPane(), "Editar").doClick();
        });

        List<JTextField> campos = buscarTodos(telaMarca.getContentPane(), JTextField.class);
        assertEquals("BMW",      campos.get(0).getText());
        assertEquals("Alemanha", campos.get(1).getText());
    }

    @Test
    void editarSemSelecaoNaoChamaController() throws Exception {
        agendarFechamentoDosDialogos();
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaMarca.getContentPane(), "Editar").doClick()
        );

        verify(controllerMock, never()).buscarMarca(anyInt());
    }

    // =============================================
    // TelaMarca — excluir
    // =============================================

    @Test
    void excluirMarcaSelecionadaChamaController() throws Exception {
        Marca marca = marcaCom(1, "Toyota", "Japão");
        when(controllerMock.listarMarcas()).thenReturn(List.of(marca));

        SwingUtilities.invokeAndWait(() -> {
            telaMarca.dispose();
            telaMarca = new TelaMarca(null, controllerMock);
        });

        agendarFechamentoDosDialogos();
        SwingUtilities.invokeAndWait(() -> {
            JTable tabela = buscarTodos(telaMarca.getContentPane(), JTable.class).get(0);
            tabela.setRowSelectionInterval(0, 0);
            buscarBotao(telaMarca.getContentPane(), "Excluir").doClick();
        });

        verify(controllerMock).excluirMarca(1);
    }

    @Test
    void excluirSemSelecaoNaoChamaController() throws Exception {
        agendarFechamentoDosDialogos();
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaMarca.getContentPane(), "Excluir").doClick()
        );

        verify(controllerMock, never()).excluirMarca(anyInt());
    }

    // =============================================
    // Demo visual — simula usuário real
    // =============================================

    @Test
    @Timeout(30)
    void demoFluxoCadastroMarca() throws Exception {
        Marca marcaSalva = marcaCom(1, "Toyota", "Japão");
        when(controllerMock.listarMarcas())
            .thenReturn(new ArrayList<>())          // antes de salvar
            .thenReturn(List.of(marcaSalva));        // após salvar

        // 1. Abre TelaInicio
        SwingUtilities.invokeAndWait(() -> {
            telaInicio.setLocationRelativeTo(null);
            telaInicio.setVisible(true);
        });
        pausa(1500);

        // 2. Clica em "Cadastrar Marca" e abre TelaMarca
        SwingUtilities.invokeAndWait(() -> {
            telaMarca.setLocationRelativeTo(telaInicio);
            telaMarca.setVisible(true);
        });
        pausa(1000);

        // 3. Digita no campo Nome, letra por letra
        List<JTextField> campos = buscarTodos(telaMarca.getContentPane(), JTextField.class);
        digitarDevagar(campos.get(0), "Toyota");
        pausa(500);

        // 4. Digita no campo País
        digitarDevagar(campos.get(1), "Japão");
        pausa(800);

        // 5. Clica em Salvar
        SwingUtilities.invokeAndWait(() ->
            buscarBotao(telaMarca.getContentPane(), "Salvar").doClick()
        );
        pausa(1000);

        // 6. Fecha o JOptionPane "Marca salva!" que apareceu
        SwingUtilities.invokeAndWait(() -> {
            for (Window w : Window.getWindows()) {
                if (w instanceof JDialog d && d != telaMarca && d.isVisible()) d.dispose();
            }
        });
        pausa(800);

        // Verifica que o controller foi chamado corretamente
        verify(controllerMock).salvarMarca("Toyota", "Japão");
    }

    // Digita cada caractere com um intervalo, simulando digitação humana
    private void digitarDevagar(JTextField campo, String texto) throws Exception {
        for (char c : texto.toCharArray()) {
            SwingUtilities.invokeAndWait(() ->
                campo.setText(campo.getText() + c)
            );
            Thread.sleep(120);
        }
    }

    private static void pausa(long ms) throws InterruptedException {
        Thread.sleep(ms);
    }

    // =============================================
    // Helpers
    // =============================================

    private void agendarFechamentoDosDialogos() {
        new Thread(() -> {
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            SwingUtilities.invokeLater(() -> {
                for (Window w : Window.getWindows()) {
                    if (w instanceof JDialog d && d != telaMarca && d.isVisible()) {
                        d.dispose();
                    }
                }
            });
        }).start();
    }

    private static JButton buscarBotao(Container container, String texto) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton b && texto.equals(b.getText())) return b;
            if (c instanceof Container sub) {
                JButton found = buscarBotao(sub, texto);
                if (found != null) return found;
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

    private static Marca marcaCom(int id, String nome, String pais) {
        Marca m = new Marca();
        m.setId(id);
        m.setNome(nome);
        m.setPais(pais);
        return m;
    }
}
