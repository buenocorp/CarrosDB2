package model;

import java.util.ArrayList;

public class Marca {

    private int id; // 🔥 NECESSÁRIO para banco
    private String nome;
    private String pais;
    private ArrayList<Modelo> modelos;

    // Construtor padrão
    public Marca() {
        modelos = new ArrayList<>();
    }

    // Construtor com parâmetros
    public Marca(int id, String nome, String pais) {
        this.id = id;
        this.nome = nome;
        this.pais = pais;
        this.modelos = new ArrayList<>();
    }

    // 🔹 Getter e Setter - ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // 🔹 Getter e Setter - nome
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // 🔹 Getter e Setter - país
    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    // 🔹 Getter da lista de modelos
    public ArrayList<Modelo> getModelos() {
        return modelos;
    }

    // 🔹 Adicionar modelo à marca (uso local/memória)
    public void adicionarModelo(Modelo modelo) {
        modelos.add(modelo);
    }

    // 🔹 Mostrar modelos da marca
    public void mostrarModelos() {
        System.out.println("Modelos da marca: " + nome);

        for (Modelo m : modelos) {
            System.out.println("- " + m.getNome());
        }
    }

    // 🔹 Muito importante para JComboBox
    @Override
    public String toString() {
        return nome;
    }
}