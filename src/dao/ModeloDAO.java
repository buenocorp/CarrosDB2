package dao;

import model.Modelo;
import model.Marca;
import database.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ModeloDAO {
	
	private Connection initConnection() {
      try {
    	  Connection conn = ConnectionFactory.getConnection();
    	  return conn;

         } catch (Exception e) {
             e.printStackTrace();
        	 return null;
         }
	}

    public void inserir(Modelo modelo, int marcaId) {
        String sql = "INSERT INTO modelo (nome, marca_id) VALUES (?, ?)";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modelo.getNome());
            stmt.setInt(2, marcaId);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(int id, Modelo modelo, int marcaId) {
        String sql = "UPDATE modelo SET nome = ?, marca_id = ? WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modelo.getNome());
            stmt.setInt(2, marcaId);
            stmt.setInt(3, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM modelo WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Modelo> listar() {
        List<Modelo> lista = new ArrayList<>();

        String sql = """
            SELECT m.id AS modelo_id,
		           m.nome AS modelo,
		           ma.id AS marca_id,
		           ma.nome AS marca,
		           ma.pais
		    FROM modelo m
		    JOIN marca ma ON m.marca_id = ma.id
        """;

        try (Connection conn = initConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

        	while (rs.next()) {

        	    Marca marca = new Marca();
        	    marca.setId(rs.getInt("marca_id"));
        	    marca.setNome(rs.getString("marca"));
        	    marca.setPais(rs.getString("pais"));

        	    Modelo modelo = new Modelo();
        	    modelo.setId(rs.getInt("modelo_id"));
        	    modelo.setNome(rs.getString("modelo"));
        	    modelo.setMarca(marca);

        	    lista.add(modelo);
        	}

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    
    public Modelo buscarPorId(int id) {

        String sql = "SELECT * FROM modelo WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Modelo modelo = new Modelo();
                modelo.setId(rs.getInt("id"));
                modelo.setNome(rs.getString("nome"));

                MarcaDAO marcaDAO = new MarcaDAO();
                int marcaId = rs.getInt("marca_id");
                Marca marca = marcaDAO.buscarPorId(marcaId);

                modelo.setMarca(marca);

                return modelo;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public void atualizar(Modelo modelo) {

        String sql = "UPDATE modelo SET nome = ?, marca_id = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, modelo.getNome());
            stmt.setInt(2, modelo.getMarca().getId());
            stmt.setInt(3, modelo.getId());

            int linhas = stmt.executeUpdate();

            if (linhas == 0) {
                System.out.println("Nenhum modelo foi atualizado.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}