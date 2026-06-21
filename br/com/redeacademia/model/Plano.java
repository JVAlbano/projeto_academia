package br.com.redeacademia.model;

/**
 * Plano de assinatura exclusivo de uma academia. Define valor mensal, duracao e
 * regra de desconto progressivo conforme a duracao do contrato.
 */
public class Plano {

    private final String id;
    private String nome;
    private double valorMensal;
    private int duracaoMeses;
    private boolean ativo;
    private final String academiaId;

    public Plano(String id, String nome, double valorMensal, int duracaoMeses, String academiaId) {
        this.id = id;
        this.nome = nome;
        setValorMensal(valorMensal);
        setDuracaoMeses(duracaoMeses);
        this.academiaId = academiaId;
        this.ativo = true;
    }

    /**
     * Desconto progressivo sobre o valor total: quanto maior a duracao, maior o
     * desconto (12+ meses: 15%; 6+ meses: 10%; 3+ meses: 5%; abaixo: sem desconto).
     *
     * @return percentual de desconto aplicado (0.0 a 0.15).
     */
    public double calcularDesconto() {
        if (duracaoMeses >= 12) {
            return 0.15;
        } else if (duracaoMeses >= 6) {
            return 0.10;
        } else if (duracaoMeses >= 3) {
            return 0.05;
        }
        return 0.0;
    }

    /** Valor total do contrato (valor mensal x duracao) ja com o desconto aplicado. */
    public double calcularValorTotalComDesconto() {
        double total = valorMensal * duracaoMeses;
        return total * (1 - calcularDesconto());
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
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

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        if (valorMensal < 0) {
            throw new IllegalArgumentException("Valor mensal nao pode ser negativo.");
        }
        this.valorMensal = valorMensal;
    }

    public int getDuracaoMeses() {
        return duracaoMeses;
    }

    public void setDuracaoMeses(int duracaoMeses) {
        if (duracaoMeses <= 0) {
            throw new IllegalArgumentException("Duracao deve ser de pelo menos 1 mes.");
        }
        this.duracaoMeses = duracaoMeses;
    }

    public String getAcademiaId() {
        return academiaId;
    }

    @Override
    public String toString() {
        return String.format("Plano{id=%s, nome=%s, valorMensal=R$%.2f, duracao=%d meses}",
                id, nome, valorMensal, duracaoMeses);
    }
}
