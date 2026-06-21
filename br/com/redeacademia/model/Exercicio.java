package br.com.redeacademia.model;

/**
 * Exercicio que compoe um {@link Treino}. Parte da composicao Treino 1--* Exercicio.
 */
public class Exercicio {

    private String nome;
    private String grupoMuscular;
    private int series;
    private int repeticoes;
    private double carga; // kg

    public Exercicio(String nome, String grupoMuscular, int series, int repeticoes, double carga) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
        setSeries(series);
        setRepeticoes(repeticoes);
        setCarga(carga);
    }

    /** Volume de treino = series x repeticoes x carga. */
    public double calcularVolume() {
        return series * repeticoes * carga;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(String grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        if (series <= 0) {
            throw new IllegalArgumentException("Series deve ser maior que zero.");
        }
        this.series = series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(int repeticoes) {
        if (repeticoes <= 0) {
            throw new IllegalArgumentException("Repeticoes deve ser maior que zero.");
        }
        this.repeticoes = repeticoes;
    }

    public double getCarga() {
        return carga;
    }

    public void setCarga(double carga) {
        if (carga < 0) {
            throw new IllegalArgumentException("Carga nao pode ser negativa.");
        }
        this.carga = carga;
    }
}
