package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    private static final String URL = "jdbc:sqlite:banco.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try (Connection conn = getConnection()) {
            System.out.println("Banco conectado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao conectar no banco!");
            e.printStackTrace();
        }
    }
}