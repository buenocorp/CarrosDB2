package controller;

import java.util.List;

import dao.ModeloDAO;
import dao.MarcaDAO;
import model.Marca;
import model.Modelo;

public class ModeloController {

    private ModeloDAO modeloDAO;
    private MarcaDAO marcaDAO;

    public ModeloController() {
        this.modeloDAO = new ModeloDAO();
        this.marcaDAO = new MarcaDAO();
    }

    public void salvarModelo(String nome, Marca marca) {

        Modelo modelo = new Modelo();
        modelo.setNome(nome);
        modelo.setMarca(marca);

        modeloDAO.inserir(modelo, marca.getId());
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
    
    public void atualizarModelo(int id, String nome, Marca marca) {
        Modelo modelo = new Modelo();
        modelo.setId(id);
        modelo.setNome(nome.trim());
        modelo.setMarca(marca);

        modeloDAO.atualizar(modelo);
    }
}