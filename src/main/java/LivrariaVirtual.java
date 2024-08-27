package main.java;

import main.java.conections.PostgreSQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class LivrariaVirtual {
    private static final int MAX_IMPRESSOS = 10;
    private static final int MAX_ELETRONICOS = 20;
    private static final int MAX_VENDAS = 50;

    private Scanner scanner = new Scanner(System.in);

    public void cadastrarLivro() {
        System.out.println("Tipo de livro (1-Impresso, 2-Eletronico, 3-Ambos): ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        if (tipo == 1 || tipo == 3) {
            if (contarRegistros("Impresso") >= MAX_IMPRESSOS) {
                System.out.println("Limite de livros impressos atingido!");
                return;
            }
            main.java.Impresso impresso = cadastrarLivroImpresso();
            salvarLivroNoBanco(impresso, "Impresso");
        }

        if (tipo == 2 || tipo == 3) {
            if (contarRegistros("Eletronico") >= MAX_ELETRONICOS) {
                System.out.println("Limite de livros eletrônicos atingido!");
                return;
            }
            main.java.Eletronico eletronico = cadastrarLivroEletronico();
            salvarLivroNoBanco(eletronico, "Eletronico");
        }
    }

    public void realizarVenda() {
        if (contarVendas() >= MAX_VENDAS) {
            System.out.println("Limite de vendas atingido!");
            return;
        }

        System.out.print("Nome do cliente: ");
        String cliente = scanner.nextLine();

        System.out.print("Quantidade de livros para comprar: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();

        main.java.Venda venda = new main.java.Venda(quantidade, cliente);
        ArrayList<Integer> livroIds = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            System.out.println("Tipo de livro (1-Impresso, 2-Eletronico): ");
            int tipo = scanner.nextInt();
            scanner.nextLine();

            if (tipo == 1) {
                listarLivrosImpressos();
                System.out.print("Escolha o ID do livro impresso: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                main.java.Livro livro = getLivroById(id, "Impresso");
                if (livro != null) {
                    livroIds.add(id);
                    venda.addLivro(livro, i);

                    main.java.Impresso livroImpresso = (main.java.Impresso) livro;
                    venda.setValor(venda.getValor() + livroImpresso.getFrete());

                } else {
                    System.out.println("ID inválido.");
                }
            } else if (tipo == 2) {
                listarLivrosEletronicos();
                System.out.print("Escolha o ID do livro eletrônico: ");
                int id = scanner.nextInt();
                scanner.nextLine();

                main.java.Livro livro = getLivroById(id, "Eletronico");
                if (livro != null) {
                    livroIds.add(id);
                    venda.addLivro(livro, i);
                } else {
                    System.out.println("ID inválido.");
                }
            } else {
                System.out.println("Tipo de livro ou quantidade inválida.");
            }
        }

        salvarVendaNoBanco(venda, livroIds);
    }

    private int contarRegistros(String tipo) {
        String sql = "SELECT COUNT(*) FROM livros WHERE tipo = ?";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tipo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar registros: " + e.getMessage());
        }
        return 0;
    }

    private int contarVendas() {
        String sql = "SELECT COUNT(*) FROM vendas";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao contar vendas: " + e.getMessage());
        }
        return 0;
    }

    private main.java.Impresso cadastrarLivroImpresso() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autores: ");
        String autores = scanner.nextLine();
        System.out.print("Editora: ");
        String editora = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        System.out.print("Frete: ");
        float frete = scanner.nextFloat();
        System.out.print("Estoque: ");
        int estoque = scanner.nextInt();
        scanner.nextLine();

        return new main.java.Impresso(titulo, autores, editora, preco, frete, estoque);
    }

    private main.java.Eletronico cadastrarLivroEletronico() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Autores: ");
        String autores = scanner.nextLine();
        System.out.print("Editora: ");
        String editora = scanner.nextLine();
        System.out.print("Preço: ");
        double preco = scanner.nextDouble();
        System.out.print("Tamanho (em KB): ");
        int tamanho = scanner.nextInt();
        scanner.nextLine();

        return new main.java.Eletronico(titulo, autores, editora, preco, tamanho);
    }

    private void salvarLivroNoBanco(main.java.Livro livro, String tipo) {
        String sql = "INSERT INTO livros(titulo, autores, editora, preco, tipo, frete, estoque, tamanho) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, livro.getTitulo());
            pstmt.setString(2, livro.getAutores());
            pstmt.setString(3, livro.getEditora());
            pstmt.setDouble(4, livro.getPreco());
            pstmt.setString(5, tipo);

            if (tipo.equals("Impresso")) {
                main.java.Impresso impresso = (main.java.Impresso) livro;
                pstmt.setDouble(6, impresso.getFrete());
                pstmt.setInt(7, impresso.getEstoque());
                pstmt.setNull(8, java.sql.Types.INTEGER);
            } else if (tipo.equals("Eletronico")) {
                main.java.Eletronico eletronico = (main.java.Eletronico) livro;
                pstmt.setNull(6, java.sql.Types.DOUBLE);
                pstmt.setNull(7, java.sql.Types.INTEGER);
                pstmt.setInt(8, eletronico.getTamanho());
            }

            pstmt.executeUpdate();
            System.out.println("Livro cadastrado no banco de dados.");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar o livro no banco de dados: " + e.getMessage());
        }
    }

    private void salvarVendaNoBanco(main.java.Venda venda, ArrayList<Integer> livroIds) {
        String sql = "INSERT INTO vendas(cliente, valor, livros) VALUES(?, ?, ?)";
        String livrosJson = livroIds.toString();

        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, venda.getCliente());
            pstmt.setDouble(2, venda.getValor());
            pstmt.setString(3, livrosJson);

            pstmt.executeUpdate();
            System.out.println("Venda registrada no banco de dados.");
        } catch (SQLException e) {
            System.out.println("Erro ao salvar a venda no banco de dados: " + e.getMessage());
        }
    }

    public void listarLivrosImpressos() {
        System.out.println("\n--- Livros Impressos ---");
        String sql = "SELECT * FROM livros WHERE tipo = 'Impresso'";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", " +
                        "Título: " + rs.getString("titulo") + ", " +
                        "Autores: " + rs.getString("autores") + ", " +
                        "Editora: " + rs.getString("editora") + ", " +
                        "Preço: " + rs.getDouble("preco") + ", " +
                        "Frete: " + rs.getDouble("frete") + ", " +
                        "Estoque: " + rs.getInt("estoque"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros impressos: " + e.getMessage());
        }
    }

    public void listarLivrosEletronicos() {
        System.out.println("\n--- Livros Eletrônicos ---");
        String sql = "SELECT * FROM livros WHERE tipo = 'Eletronico'";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", " +
                        "Título: " + rs.getString("titulo") + ", " +
                        "Autores: " + rs.getString("autores") + ", " +
                        "Editora: " + rs.getString("editora") + ", " +
                        "Preço: " + rs.getDouble("preco") + ", " +
                        "Tamanho: " + rs.getInt("tamanho") + "KB");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar livros eletrônicos: " + e.getMessage());
        }
    }

    public void listarLivros() {
        listarLivrosImpressos();
        listarLivrosEletronicos();
    }

    public void listarVendas() {
        System.out.println("\n--- Vendas Realizadas ---");
        String sql = "SELECT * FROM vendas";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", " +
                        "Cliente: " + rs.getString("cliente") + ", " +
                        "Valor: " + rs.getDouble("valor") + ", " +
                        "Livros: " + rs.getString("livros"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar vendas: " + e.getMessage());
        }
    }

    private main.java.Livro getLivroById(int id, String tipo) {
        String sql = "SELECT * FROM livros WHERE id = ? AND tipo = ?";
        try (Connection conn = PostgreSQLConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, tipo);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (tipo.equals("Impresso")) {
                    return new main.java.Impresso(rs.getString("titulo"),
                            rs.getString("autores"),
                            rs.getString("editora"),
                            rs.getDouble("preco"),
                            rs.getFloat("frete"),
                            rs.getInt("estoque"));
                } else if (tipo.equals("Eletronico")) {
                    return new main.java.Eletronico(rs.getString("titulo"),
                            rs.getString("autores"),
                            rs.getString("editora"),
                            rs.getDouble("preco"),
                            rs.getInt("tamanho"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter o livro pelo ID: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        PostgreSQLConnection.createTables();
        LivrariaVirtual livraria = new LivrariaVirtual();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1) Cadastrar livro");
            System.out.println("2) Realizar uma venda");
            System.out.println("3) Listar livros");
            System.out.println("4) Listar vendas");
            System.out.println("5) Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    livraria.cadastrarLivro();
                    break;
                case 2:
                    livraria.realizarVenda();
                    break;
                case 3:
                    livraria.listarLivros();
                    break;
                case 4:
                    livraria.listarVendas();
                    break;
                case 5:
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }
}
