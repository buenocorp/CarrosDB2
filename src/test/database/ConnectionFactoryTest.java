package test.database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import database.ConnectionFactory;

public class ConnectionFactoryTest {

    @Test
    public void testeGetConnectionRetornaConexaoAberta() throws Exception {
        Connection conn = ConnectionFactory.getConnection();

        assertNotNull(conn);
        assertFalse(conn.isClosed());

        conn.close();
    }

    @Test
    public void testeGetConnectionRetornaNovaInstanciaACadaChamada() throws Exception {
        Connection conn1 = ConnectionFactory.getConnection();
        Connection conn2 = ConnectionFactory.getConnection();

        assertNotSame(conn1, conn2);

        conn1.close();
        conn2.close();
    }

    @Test
    public void testeFecharConexaoFunciona() throws Exception {
        Connection conn = ConnectionFactory.getConnection();

        conn.close();

        assertTrue(conn.isClosed());
    }

    @Test
    public void testeInitNaoLancaExcecao() {
        assertDoesNotThrow(() -> ConnectionFactory.init());
    }
}
