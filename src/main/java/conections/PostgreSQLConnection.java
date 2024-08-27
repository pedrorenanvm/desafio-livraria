package main.java.conections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgreSQLConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/livraria_virtual";
    private static final String USER = "postgres";
    private static final String PASSWORD = "963852741";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void createTables() {
        // Criando a sequência manualmente, iniciando em 0
        String sqlSequence = "CREATE SEQUENCE IF NOT EXISTS livros_id_seq START 0";

        // Alterando a criação da tabela para usar a sequência manual
        String sqlLivros = "CREATE TABLE IF NOT EXISTS livros (" +
                "id INTEGER PRIMARY KEY DEFAULT nextval('livros_id_seq'), " +
                "titulo VARCHAR(255), " +
                "autores VARCHAR(255), " +
                "editora VARCHAR(255), " +
                "preco DOUBLE PRECISION, " +
                "tipo VARCHAR(50), " +
                "frete DOUBLE PRECISION, " +
                "estoque INTEGER, " +
                "tamanho INTEGER)";

        String sqlVendas = "CREATE TABLE IF NOT EXISTS vendas (" +
                "id SERIAL PRIMARY KEY, " +
                "cliente VARCHAR(255), " +
                "valor DOUBLE PRECISION, " +
                "livros TEXT)";

        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmtSequence = conn.prepareStatement(sqlSequence);
             PreparedStatement pstmtLivros = conn.prepareStatement(sqlLivros);
             PreparedStatement pstmtVendas = conn.prepareStatement(sqlVendas)) {
            // Executa a criação da sequência
            pstmtSequence.executeUpdate();
            // Executa a criação da tabela de livros
            pstmtLivros.executeUpdate();
            // Executa a criação da tabela de vendas
            pstmtVendas.executeUpdate();
            System.out.println("Tabelas criadas no PostgreSQL.");
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabelas no banco de dados: " + e.getMessage());
        }
    }
}
