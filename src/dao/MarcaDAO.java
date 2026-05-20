package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import database.ConnectionFactory;
import model.Marca;

public class MarcaDAO {

    private final Connection connection;

    public MarcaDAO(Connection connection) {
        this.connection = connection;
    }

    public MarcaDAO() throws Exception {
        this.connection = ConnectionFactory.getConnection();
    }

    public void inserir(Marca marca) {

        String sql =
            "INSERT INTO marca (nome, pais) VALUES (?, ?)";

        try (PreparedStatement stmt =
                 connection.prepareStatement(sql)) {

            stmt.setString(1, marca.getNome());
            stmt.setString(2, marca.getPais());

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void atualizar(int id, Marca marca) {

        String sql =
            "UPDATE marca SET nome = ?, pais = ? WHERE id = ?";

        try (PreparedStatement stmt =
                 connection.prepareStatement(sql)) {

            stmt.setString(1, marca.getNome());
            stmt.setString(2, marca.getPais());
            stmt.setInt(3, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excluir(int id) {

        String sql =
            "DELETE FROM marca WHERE id = ?";

        try (PreparedStatement stmt =
                 connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Marca> listar() {

        List<Marca> lista = new ArrayList<>();

        String sql =
            "SELECT * FROM marca";

        try (Statement stmt =
                 connection.createStatement();

             ResultSet rs =
                 stmt.executeQuery(sql)) {

            while (rs.next()) {

                Marca marca = new Marca();

                marca.setId(rs.getInt("id"));
                marca.setNome(rs.getString("nome"));
                marca.setPais(rs.getString("pais"));

                lista.add(marca);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public Marca buscarPorId(int id) {

        String sql =
            "SELECT * FROM marca WHERE id = ?";

        try (PreparedStatement stmt =
                 connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs =
                     stmt.executeQuery()) {

                if (rs.next()) {

                    Marca marca = new Marca();

                    marca.setId(rs.getInt("id"));
                    marca.setNome(rs.getString("nome"));
                    marca.setPais(rs.getString("pais"));

                    return marca;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}