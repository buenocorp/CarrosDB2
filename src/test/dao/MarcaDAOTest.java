package test.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.MarcaDAO;
import model.Marca;

public class MarcaDAOTest {

    private Connection connection;

    private PreparedStatement preparedStatement;

    private Statement statement;

    private ResultSet resultSet;

    private MarcaDAO marcaDAO;

    @BeforeEach
    public void setup() throws Exception {

        connection = mock(Connection.class);

        preparedStatement =
            mock(PreparedStatement.class);

        statement = mock(Statement.class);

        resultSet = mock(ResultSet.class);

        marcaDAO =
            new MarcaDAO(connection);
    }

    @Test
    public void testeInserirMarca()
        throws Exception {

        when(
            connection.prepareStatement(anyString())
        ).thenReturn(preparedStatement);

        Marca marca = new Marca();

        marca.setNome("Toyota");

        marca.setPais("Japão");

        marcaDAO.inserir(marca);

        verify(preparedStatement)
            .setString(1, "Toyota");

        verify(preparedStatement)
            .setString(2, "Japão");

        verify(preparedStatement)
            .executeUpdate();
    }

    @Test
    public void testeAtualizarMarca()
        throws Exception {

        when(
            connection.prepareStatement(anyString())
        ).thenReturn(preparedStatement);

        Marca marca = new Marca();

        marca.setNome("Honda");

        marca.setPais("Japão");

        marcaDAO.atualizar(1, marca);

        verify(preparedStatement)
            .setString(1, "Honda");

        verify(preparedStatement)
            .setString(2, "Japão");

        verify(preparedStatement)
            .setInt(3, 1);

        verify(preparedStatement)
            .executeUpdate();
    }

    @Test
    public void testeExcluirMarca()
        throws Exception {

        when(
            connection.prepareStatement(anyString())
        ).thenReturn(preparedStatement);

        marcaDAO.excluir(1);

        verify(preparedStatement)
            .setInt(1, 1);

        verify(preparedStatement)
            .executeUpdate();
    }

    @Test
    public void testeListarMarcas()
        throws Exception {

        when(connection.createStatement())
            .thenReturn(statement);

        when(statement.executeQuery(anyString()))
            .thenReturn(resultSet);

        when(resultSet.next())
            .thenReturn(true, false);

        when(resultSet.getInt("id"))
            .thenReturn(1);

        when(resultSet.getString("nome"))
            .thenReturn("Ford");

        when(resultSet.getString("pais"))
            .thenReturn("EUA");

        List<Marca> lista =
            marcaDAO.listar();

        assertEquals(1, lista.size());

        assertEquals(
            "Ford",
            lista.get(0).getNome()
        );
    }

    @Test
    public void testeBuscarPorId()
        throws Exception {

        when(
            connection.prepareStatement(anyString())
        ).thenReturn(preparedStatement);

        when(preparedStatement.executeQuery())
            .thenReturn(resultSet);

        when(resultSet.next())
            .thenReturn(true);

        when(resultSet.getInt("id"))
            .thenReturn(1);

        when(resultSet.getString("nome"))
            .thenReturn("BMW");

        when(resultSet.getString("pais"))
            .thenReturn("Alemanha");

        Marca marca =
            marcaDAO.buscarPorId(1);

        assertNotNull(marca);

        assertEquals(
            "BMW",
            marca.getNome()
        );
    }
}