package controller;

import java.util.List;

import dao.MarcaDAO;
import model.Marca;

public class MarcaController {

    private final MarcaDAO marcaDAO;

    public MarcaController() {
        this.marcaDAO = new MarcaDAO();
    }

    public void salvarMarca(String nome, String pais) {
        Marca marca = new Marca();
        marca.setNome(nome.trim());
        marca.setPais(pais.trim());

        marcaDAO.inserir(marca);
    }

    public void atualizarMarca(int id, String nome, String pais) {
        Marca marca = new Marca();
        marca.setId(id);
        marca.setNome(nome.trim());
        marca.setPais(pais.trim());

        marcaDAO.atualizar(id, marca);
    }

    public void excluirMarca(int id) {
        marcaDAO.excluir(id);
    }

    public List<Marca> listarMarcas() {
    	return marcaDAO.listar();
    }

    public Marca buscarMarca(int id) {
    	return marcaDAO.buscarPorId(id);
    }
}