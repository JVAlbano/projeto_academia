package model;

import enums.TipoFuncionario;
import java.time.LocalDate;

public class Funcionario extends Pessoa {
    private TipoFuncionario tipo;
    private double salario;
    private LocalDate dataAdmissao;
    private String especialidade;

    public Funcionario(String id, String nome, String cpf, String telefone, String email,
                       TipoFuncionario tipo, double salario, LocalDate dataAdmissao, String especialidade) {
        super(id, nome, cpf, telefone, email);
        this.tipo = tipo;
        this.salario = salario;
        this.dataAdmissao = dataAdmissao;
        this.especialidade = especialidade;
    }

    public String calcularRemuneracao() {
        double bonus = switch (tipo) {
            case GERENTE -> salario * 0.20;
            case INSTRUTOR -> salario * 0.10;
            case RECEPCIONISTA -> salario * 0.05;
        };
        return String.format("Salário base: R$%.2f | Bônus: R$%.2f | Total: R$%.2f",
                salario, bonus, salario + bonus);
    }

    public TipoFuncionario getTipo() {
        return tipo;
    }

    public double getSalario() {
        return salario;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public String getEspecialidade() {
        return especialidade;
    }
}
