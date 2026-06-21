package br.com.redeacademia.model;

import br.com.redeacademia.model.enums.NivelTreino;

import java.util.ArrayList;
import java.util.List;

/**
 * Treino montado por um instrutor e vinculado a um cliente. Composto por uma lista
 * de {@link Exercicio} (composicao: os exercicios so existem dentro do treino).
 */
public class Treino {

    private final String id;
    private String nome;
    private String objetivo;
    private NivelTreino nivel;
    private int duracaoMinutos;
    private final String clienteId;
    private final String instrutorId;
    private final List<Exercicio> exercicios = new ArrayList<>();

    public Treino(String id, String nome, String objetivo, NivelTreino nivel,
                  int duracaoMinutos, String clienteId, String instrutorId) {
        this.id = id;
        this.nome = nome;
        this.objetivo = objetivo;
        this.nivel = nivel;
        setDuracaoMinutos(duracaoMinutos);
        this.clienteId = clienteId;
        this.instrutorId = instrutorId;
    }

    public void adicionarExercicio(Exercicio exercicio) {
        exercicios.add(exercicio);
    }

    public boolean removerExercicio(String nomeExercicio) {
        return exercicios.removeIf(e -> e.getNome().equalsIgnoreCase(nomeExercicio));
    }

    /** Carga total do treino = soma do volume de todos os exercicios. */
    public double calcularCargaTotal() {
        return exercicios.stream().mapToDouble(Exercicio::calcularVolume).sum();
    }

    public List<Exercicio> getExercicios() {
        return exercicios;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public NivelTreino getNivel() {
        return nivel;
    }

    public void setNivel(NivelTreino nivel) {
        this.nivel = nivel;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        if (duracaoMinutos <= 0) {
            throw new IllegalArgumentException("Duracao do treino deve ser maior que zero.");
        }
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getInstrutorId() {
        return instrutorId;
    }
}
