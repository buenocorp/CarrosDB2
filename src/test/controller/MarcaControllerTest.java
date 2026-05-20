package test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.MarcaController;
import dao.MarcaDAO;
import model.Marca;

public class MarcaControllerTest {

    private MarcaDAO marcaDAO;
    private MarcaController controller;

    @BeforeEach
    public void setup() {

        marcaDAO = mock(MarcaDAO.class);

        controller = new MarcaController(marcaDAO);
    }

    @Test
    public void testeSalvarMarca() {

        controller.salvarMarca("Toyota", "Japão");

        verify(marcaDAO, times(1))
            .inserir(any(Marca.class));
    }

    @Test
    public void testeListarMarcas() {

        List<Marca> listaFake = new ArrayList<>();

        Marca marca = new Marca();
        marca.setId(1);
        marca.setNome("Honda");
        marca.setPais("Japão");

        listaFake.add(marca);

        when(marcaDAO.listar())
            .thenReturn(listaFake);

        List<Marca> resultado =
            controller.listarMarcas();

        assertEquals(1, resultado.size());

        assertEquals(
            "Honda",
            resultado.get(0).getNome()
        );
    }

    @Test
    public void testeBuscarMarca() {

        Marca marca = new Marca();

        marca.setId(1);
        marca.setNome("BMW");
        marca.setPais("Alemanha");

        when(marcaDAO.buscarPorId(1))
            .thenReturn(marca);

        Marca resultado =
            controller.buscarMarca(1);

        assertNotNull(resultado);

        assertEquals(
            "BMW",
            resultado.getNome()
        );
    }

    @Test
    public void testeAtualizarMarca() {

        controller.atualizarMarca(
            1,
            "BMW Atualizada",
            "Alemanha"
        );

        verify(marcaDAO, times(1))
            .atualizar(eq(1), any(Marca.class));
    }

    @Test
    public void testeExcluirMarca() {

        controller.excluirMarca(1);

        verify(marcaDAO, times(1))
            .excluir(1);
    }
}