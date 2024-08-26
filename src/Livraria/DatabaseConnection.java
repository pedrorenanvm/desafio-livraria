package Livraria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/livraria_virtual";
    private static final String USER = "root";
    private static final String PASS = "password";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Conectado ao banco de dados MySQL.");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados MySQL: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String sqlLivros = "CREATE TABLE IF NOT EXISTS livros (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "titulo VARCHAR(255) NOT NULL," +
                "autores VARCHAR(255) NOT NULL," +
                "editora VARCHAR(255) NOT NULL," +
                "preco DOUBLE NOT NULL," +
                "tipo VARCHAR(50) NOT NULL," +
                "frete DOUBLE," +
                "estoque INT," +
                "tamanho INT);";

        String sqlVendas = "CREATE TABLE IF NOT EXISTS vendas (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "cliente VARCHAR(255) NOT NULL," +
                "valor DOUBLE NOT NULL," +
                "livros TEXT NOT NULL);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLivros);
            stmt.execute(sqlVendas);
            System.out.println("Tabelas criadas com sucesso no banco de dados MySQL.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar as tabelas: " + e.getMessage());
        }
    }
}
