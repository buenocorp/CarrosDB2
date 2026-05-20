package controller;

import java.util.List;

import dao.MarcaDAO;
import dao.ModeloDAO;
import model.Marca;
import model.Modelo;

public class ModeloController {

    private final MarcaDAO marcaDAO;

    private final ModeloDAO modeloDAO;

    public ModeloController(
        ModeloDAO modeloDAO,
        MarcaDAO marcaDAO
    ) {

        this.modeloDAO = modeloDAO;
        this.marcaDAO = marcaDAO;
    }

    public void salvarModelo(
        String nome,
        Marca marca
    ) {

        Modelo modelo = new Modelo();

        modelo.setNome(nome.trim());
        modelo.setMarca(marca);

        modeloDAO.inserir(
            modelo,
            marca.getId()
        );
    }

    public void excluirModelo(int id) {
        modeloDAO.excluir(id);
    }

    public List<Modelo> listarModelos() {
        return modeloDAO.listar();
    }

    public List<Marca> listarMarcas() {
        return marcaDAO.listar();
    }

    public Modelo buscarModelo(int id) {
        return modeloDAO.buscarPorId(id);
    }

    public void atualizarModelo(
        int id,
        String nome,
        Marca marca
    ) {

        Modelo modelo = new Modelo();

        modelo.setId(id);

        modelo.setNome(nome.trim());

        modelo.setMarca(marca);

        modeloDAO.atualizar(modelo);
    }
}