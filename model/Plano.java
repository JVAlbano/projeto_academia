package model;

public class Plano {
    private String id;
    private String nome;
    private String descricao;
    private double valorMensal;
    private int duracaoMeses;
    private boolean incluiAvaliacaoFisica;
    private boolean incluiAulaPersonal;
    private String filialId;

    public Plano(String id, String nome, String descricao, double valorMensal,
                 int duracaoMeses, boolean incluiAvaliacaoFisica, boolean incluiAulaPersonal, String filialId) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valorMensal = valorMensal;
        this.duracaoMeses = duracaoMeses;
        this.incluiAvaliacaoFisica = incluiAvaliacaoFisica;
        this.incluiAulaPersonal = incluiAulaPersonal;
        this.filialId = filialId;
    }

    public double calcularValorTotal() {
        return valorMensal * duracaoMeses;
    }

    public double calcularValorComDesconto(double percentualDesconto) {
        if (percentualDesconto < 0 || percentualDesconto > 100)
            throw new IllegalArgumentException("Percentual de desconto inválido: " + percentualDesconto);
        return calcularValorTotal() * (1 - percentualDesconto / 100);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public int getDuracaoMeses() {
        return duracaoMeses;
    }

    public boolean isIncluiAvaliacaoFisica() {
        return incluiAvaliacaoFisica;
    }

    public boolean isIncluiAulaPersonal() {
        return incluiAulaPersonal;
    }

    public String getFilialId() {
        return filialId;
    }
}
