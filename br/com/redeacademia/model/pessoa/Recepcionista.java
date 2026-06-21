package br.com.redeacademia.model.pessoa;

import br.com.redeacademia.model.AcessoRegistro;
import br.com.redeacademia.model.enums.TipoAcesso;
import br.com.redeacademia.model.enums.TurnoFuncionario;

import java.time.LocalDateTime;

/**
 * Recepcionista: funcionario operacional que registra acessos de clientes.
 * Comissao = salario + adicional fixo, acrescido de adicional noturno no turno NOTURNO.
 */
public class Recepcionista extends Funcionario {

    private static final double ADICIONAL_FIXO = 150.0;
    private static final double ADICIONAL_NOTURNO = 400.0;

    private TurnoFuncionario turno;
    private String senhaAcesso;

    // --- Dados de contexto preenchidos pelos services (nao persistidos) ---
    private int totalEntradas;
    private int totalSaidas;
    private String horarioPico = "-";

    public Recepcionista(String id, String nome, String cpf, String email, String telefone,
                         String matriculaFunc, double salario, String academiaId,
                         TurnoFuncionario turno, String senhaAcesso) {
        super(id, nome, cpf, email, telefone, matriculaFunc, salario, academiaId);
        this.turno = turno;
        this.senhaAcesso = senhaAcesso;
    }

    @Override
    public double calcularComissao() {
        double total = getSalario() + ADICIONAL_FIXO;
        if (turno == TurnoFuncionario.NOTURNO) {
            total += ADICIONAL_NOTURNO;
        }
        return total;
    }

    @Override
    public String gerarRelatorio() {
        return String.format(
                "=== Relatorio da Recepcao - %s ===%n"
                        + "Turno: %s%n"
                        + "Entradas registradas: %d%n"
                        + "Saidas registradas: %d%n"
                        + "Horario de pico: %s",
                getNome(), turno, totalEntradas, totalSaidas, horarioPico);
    }

    @Override
    public String getTipo() {
        return "Recepcionista";
    }

    /** Registra um acesso de cliente (cross-class: cria um {@link AcessoRegistro}). */
    public AcessoRegistro registrarAcesso(String id, String clienteId, TipoAcesso tipo) {
        return new AcessoRegistro(id, clienteId, getAcademiaId(), LocalDateTime.now(), tipo);
    }

    public TurnoFuncionario getTurno() {
        return turno;
    }

    public void setTurno(TurnoFuncionario turno) {
        this.turno = turno;
    }

    public String getSenhaAcesso() {
        return senhaAcesso;
    }

    public void setSenhaAcesso(String senhaAcesso) {
        this.senhaAcesso = senhaAcesso;
    }

    /** Preenche os dados de contexto usados por {@link #gerarRelatorio()}. */
    public void preencherDadosAcessos(int totalEntradas, int totalSaidas, String horarioPico) {
        this.totalEntradas = totalEntradas;
        this.totalSaidas = totalSaidas;
        this.horarioPico = horarioPico;
    }
}
