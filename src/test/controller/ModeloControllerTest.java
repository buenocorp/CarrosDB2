package test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import controller.ModeloController;
import dao.MarcaDAO;
import dao.ModeloDAO;
import model.Marca;
import model.Modelo;

public class ModeloControllerTest {

    private ModeloController controller;

    private ModeloDAO modeloDAO;

    private MarcaDAO marcaDAO;

    @BeforeEach
    public void setup() {

        modeloDAO = mock(ModeloDAO.class);

        marcaDAO = mock(MarcaDAO.class);

        controller =
            new ModeloController(
                modeloDAO,
                marcaDAO
            );
    }

    @Test
    public void testeSalvarModelo() {

        Marca marca = new Marca();

        marca.setId(1);

        marca.setNome("Toyota");

        controller.salvarModelo(
            "Corolla",
            marca
        );

        verify(modeloDAO, times(1))
            .inserir(any(Modelo.class), eq(1));
    }

    @Test
    public void testeListarModelos() {

        List<Modelo> listaFake =
            new ArrayList<>();

        Marca marca = new Marca();

        marca.setId(1);

        marca.setNome("Honda");

        Modelo modelo = new Modelo();

        modelo.setId(1);

        modelo.setNome("Civic");

        modelo.setMarca(marca);

        listaFake.add(modelo);

        when(modeloDAO.listar())
            .thenReturn(listaFake);

        List<Modelo> resultado =
            controller.listarModelos();

        assertEquals(
            1,
            resultado.size()
        );

        assertEquals(
            "Civic",
            resultado.get(0).getNome()
        );
    }

    @Test
    public void testeBuscarModelo() {

        Marca marca = new Marca();

        marca.setId(1);

        marca.setNome("Ford");

        Modelo modelo = new Modelo();

        modelo.setId(1);

        modelo.setNome("Focus");

        modelo.setMarca(marca);

        when(modeloDAO.buscarPorId(1))
            .thenReturn(modelo);

        Modelo resultado =
            controller.buscarModelo(1);

        assertNotNull(resultado);

        assertEquals(
            "Focus",
            resultado.getNome()
        );
    }

    @Test
    public void testeAtualizarModelo() {

        Marca marca = new Marca();

        marca.setId(1);

        marca.setNome("BMW");

        controller.atualizarModelo(
            1,
            "X1",
            marca
        );

        verify(modeloDAO, times(1))
            .atualizar(any(Modelo.class));
    }

    @Test
    public void testeExcluirModelo() {

        controller.excluirModelo(1);

        verify(modeloDAO, times(1))
            .excluir(1);
    }

    @Test
    public void testeListarMarcas() {

        List<Marca> listaFake =
            new ArrayList<>();

        Marca marca = new Marca();

        marca.setId(1);

        marca.setNome("Chevrolet");

        listaFake.add(marca);

        when(marcaDAO.listar())
            .thenReturn(listaFake);

        List<Marca> resultado =
            controller.listarMarcas();

        assertEquals(
            1,
            resultado.size()
        );

        assertEquals(
            "Chevrolet",
            resultado.get(0).getNome()
        );
    }
}