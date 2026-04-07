package model;

public class Modelo {

    private int id;
    private String nome;
    private Marca marca;

    // Construtor padrão
    public Modelo() {
    }

    // Construtor para INSERT (sem ID)
    public Modelo(String nome, Marca marca) {
        this.nome = nome;
        this.marca = marca;
    }

    // Construtor completo (usado ao buscar do banco)
    public Modelo(int id, String nome, Marca marca) {
        this.id = id;
        this.nome = nome;
        this.marca = marca;
    }

    // Getter e Setter - ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter e Setter - nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Getter e Setter - marca
    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    // Mostrar dados no console
    public void mostrarDados() {
        System.out.println("Modelo: " + nome);

        if (marca != null) {
            System.out.println("Marca: " + marca.getNome());
        }
    }

    // Útil para listas, combo ou tabela
    @Override
    public String toString() {
        return (marca != null)
                ? nome + " (" + marca.getNome() + ")"
                : nome;
    }
}