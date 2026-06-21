package br.com.redeacademia.model.pessoa;

import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.NivelTreino;

/**
 * Instrutor: funcionario operacional que acompanha alunos e monta treinos.
 * Comissao = percentual sobre a soma dos planos mensais dos alunos acompanhados.
 */
public class Instrutor extends Funcionario {

    /** Percentual de comissao sobre os planos dos alunos acompanhados. */
    private static final double PERCENTUAL_COMISSAO = 0.05;

    private String cref;          // registro profissional
    private String especialidade;
    private int cargaHoraria;     // horas semanais

    // --- Dados de contexto preenchidos pelos services (nao persistidos) ---
    private double somaPlanosAlunos;
    private int qtdAlunos;
    private int qtdTreinos;
    private double cargaMediaTreinos;

    public Instrutor(String id, String nome, String cpf, String email, String telefone,
                     String matriculaFunc, double salario, String academiaId,
                     String cref, String especialidade, int cargaHoraria) {
        super(id, nome, cpf, email, telefone, matriculaFunc, salario, academiaId);
        this.cref = cref;
        this.especialidade = especialidade;
        setCargaHoraria(cargaHoraria);
    }

    @Override
    public double calcularComissao() {
        return getSalario() + (somaPlanosAlunos * PERCENTUAL_COMISSAO);
    }

    @Override
    public String gerarRelatorio() {
        return String.format(
                "=== Relatorio do Instrutor - %s ===%n"
                        + "Especialidade: %s | CREF: %s%n"
                        + "Alunos acompanhados: %d%n"
                        + "Treinos montados: %d%n"
                        + "Carga media dos treinos: %.2f kg",
                getNome(), especialidade, cref, qtdAlunos, qtdTreinos, cargaMediaTreinos);
    }

    @Override
    public String getTipo() {
        return "Instrutor";
    }

    /** Monta um novo treino com os dados informados (cross-class: cria um {@link Treino}). */
    public Treino montarTreino(String id, String nome, String objetivo, NivelTreino nivel,
                               int duracaoMinutos, String clienteId) {
        Treino treino = new Treino(id, nome, objetivo, nivel, duracaoMinutos, clienteId, getId());
        return treino;
    }

    public String getCref() {
        return cref;
    }

    public void setCref(String cref) {
        this.cref = cref;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        if (cargaHoraria < 0) {
            throw new IllegalArgumentException("Carga horaria nao pode ser negativa.");
        }
        this.cargaHoraria = cargaHoraria;
    }

    /** Preenche os dados de contexto usados por {@link #gerarRelatorio()} e {@link #calcularComissao()}. */
    public void preencherDadosAcompanhamento(double somaPlanosAlunos, int qtdAlunos,
                                             int qtdTreinos, double cargaMediaTreinos) {
        this.somaPlanosAlunos = somaPlanosAlunos;
        this.qtdAlunos = qtdAlunos;
        this.qtdTreinos = qtdTreinos;
        this.cargaMediaTreinos = cargaMediaTreinos;
    }
}
