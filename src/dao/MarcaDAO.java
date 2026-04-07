package dao;

import model.Marca;
import database.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarcaDAO {
	
	private Connection initConnection() {
	      try {
	    	  Connection conn = ConnectionFactory.getConnection();
	    	  return conn;

	         } catch (Exception e) {
	             e.printStackTrace();
	        	 return null;
	         }
		}

    public void inserir(Marca marca) {
        String sql = "INSERT INTO marca (nome, pais) VALUES (?, ?)";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, marca.getNome());
            stmt.setString(2, marca.getPais());
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(int id, Marca marca) {
        String sql = "UPDATE marca SET nome = ?, pais = ? WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, marca.getNome());
            stmt.setString(2, marca.getPais());
            stmt.setInt(3, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM marca WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Marca> listar() {
        List<Marca> lista = new ArrayList<>();
        String sql = "SELECT * FROM marca";

        try (Connection conn = initConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Marca m = new Marca();
                m.setId(rs.getInt("id"));
                m.setNome(rs.getString("nome"));
                m.setPais(rs.getString("pais"));
                lista.add(m);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public Marca buscarPorId(int id) {

        String sql = "SELECT * FROM marca WHERE id = ?";

        try (Connection conn = initConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Marca marca = new Marca();
                marca.setId(rs.getInt("id"));
                marca.setNome(rs.getString("nome"));
                marca.setPais(rs.getString("pais"));
                return marca;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}