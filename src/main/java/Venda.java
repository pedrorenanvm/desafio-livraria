package main.java;

public class Venda {
    private main.java.Livro[] livros;
    private static int numVendas = 0;
    private int numero;
    private String cliente;
    private float valor;

    public Venda(int tamanho, String cliente) {
        this.livros = new main.java.Livro[tamanho];
        this.cliente = cliente;
        this.numero = ++numVendas;
        this.valor = 0.0f;
    }

    public void addLivro(main.java.Livro l, int index) {
        if (index >= 0 && index < livros.length) {
            livros[index] = l;
            valor += l.getPreco();
        } else {
            System.out.println("Index inválido para adicionar o livro.");
        }
    }

    public void setValor(float valor) {
        this.valor = valor;
    }

    public void listarLivros() {
        System.out.println("Livros na venda número " + numero + ":");
        for (main.java.Livro livro : livros) {
            if (livro != null) {
                System.out.println(livro);
            }
        }
    }

    public main.java.Livro[] getLivros() {
        return livros;
    }

    public static int getNumVendas() {
        return numVendas;
    }

    public int getNumero() {
        return numero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public float getValor() {
        return valor;
    }
}
