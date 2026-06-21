package br.com.redeacademia.model.pessoa;

import java.time.LocalDate;
import java.time.Period;

/**
 * Cliente da academia. Herda de {@link Pessoa} acrescentando dados de saude e
 * objetivo de treino, com comportamentos proprios (calculo de IMC e idade).
 */
public class Cliente extends Pessoa {

    private LocalDate dataNascimento;
    private String objetivo;
    private double peso;   // kg
    private double altura; // metros
    private boolean ativo;
    private String academiaId;

    public Cliente(String id, String nome, String cpf, String email, String telefone,
                   LocalDate dataNascimento, String objetivo, double peso, double altura,
                   String academiaId) {
        super(id, nome, cpf, email, telefone);
        this.dataNascimento = dataNascimento;
        this.objetivo = objetivo;
        setPeso(peso);
        setAltura(altura);
        this.academiaId = academiaId;
        this.ativo = true;
    }

    /** Indice de Massa Corporal = peso / altura^2. */
    public double calcularIMC() {
        if (altura <= 0) {
            throw new IllegalArgumentException("Altura deve ser maior que zero para calcular o IMC.");
        }
        return peso / (altura * altura);
    }

    /** Classificacao textual do IMC segundo a OMS. */
    public String classificarImc() {
        double imc = calcularIMC();
        if (imc < 18.5) {
            return "Abaixo do peso";
        } else if (imc < 25) {
            return "Peso normal";
        } else if (imc < 30) {
            return "Sobrepeso";
        } else {
            return "Obesidade";
        }
    }

    /** Idade atual em anos completos. */
    public int calcularIdade() {
        if (dataNascimento == null) {
            return 0;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    /** @return true se o cadastro do cliente esta ativo (nao desativado). */
    public boolean verificarAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("Peso deve ser maior que zero.");
        }
        this.peso = peso;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        if (altura <= 0) {
            throw new IllegalArgumentException("Altura deve ser maior que zero.");
        }
        this.altura = altura;
    }

    public String getAcademiaId() {
        return academiaId;
    }

    public void setAcademiaId(String academiaId) {
        this.academiaId = academiaId;
    }
}
