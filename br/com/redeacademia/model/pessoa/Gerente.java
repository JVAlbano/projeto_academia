package br.com.redeacademia.model.pessoa;

import br.com.redeacademia.exception.AcessoNegadoException;
import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.enums.NivelAcesso;

/**
 * Gerente: funcionario com poder administrativo sobre uma academia.
 * Unico perfil autorizado a contratar/demitir funcionarios da sua unidade (RN04).
 */
public class Gerente extends Funcionario {

    /** Valor de bonus por funcionario ativo gerenciado. */
    private static final double BONUS_POR_FUNCIONARIO = 50.0;

    private NivelAcesso nivelAcesso;
    private double bonusGestao; // percentual sobre o salario (ex.: 0.25 = 25%)

    // --- Dados de contexto preenchidos pelos services antes do calculo/relatorio (nao persistidos) ---
    private int funcionariosAtivos;
    private double receitaAcademia;
    private int clientesInadimplentes;
    private int clientesAtivos;

    public Gerente(String id, String nome, String cpf, String email, String telefone,
                   String matriculaFunc, double salario, String academiaId,
                   NivelAcesso nivelAcesso, double bonusGestao) {
        super(id, nome, cpf, email, telefone, matriculaFunc, salario, academiaId);
        this.nivelAcesso = nivelAcesso;
        setBonusGestao(bonusGestao);
    }

    @Override
    public double calcularComissao() {
        return getSalario() + (getSalario() * bonusGestao) + (funcionariosAtivos * BONUS_POR_FUNCIONARIO);
    }

    @Override
    public String gerarRelatorio() {
        return String.format(
                "=== Relatorio Gerencial - %s ===%n"
                        + "Nivel de acesso: %s%n"
                        + "Funcionarios ativos: %d%n"
                        + "Clientes ativos: %d%n"
                        + "Clientes inadimplentes: %d%n"
                        + "Receita mensal da academia: R$ %.2f",
                getNome(), nivelAcesso, funcionariosAtivos, clientesAtivos,
                clientesInadimplentes, receitaAcademia);
    }

    @Override
    public String getTipo() {
        return "Gerente";
    }

    /**
     * Contrata um funcionario, vinculando-o a academia indicada (define o academiaId
     * do novo funcionario). Valida a autorizacao (RN04). A persistencia fica a cargo
     * do service que orquestra a operacao.
     *
     * @throws AcessoNegadoException se este gerente nao for o gerente da academia.
     */
    public void contratarFuncionario(Academia academia, Funcionario novo) {
        autorizarOperacaoNa(academia, "contratar funcionarios");
        novo.setAcademiaId(academia.getId());
    }

    /**
     * Demite um funcionario, desvinculando-o da academia. Valida a autorizacao (RN04).
     * A remocao efetiva do repositorio fica a cargo do service.
     *
     * @throws AcessoNegadoException se este gerente nao for o gerente da academia.
     */
    public void demitirFuncionario(Academia academia, Funcionario alvo) {
        autorizarOperacaoNa(academia, "demitir funcionarios");
        alvo.setAcademiaId(null);
    }

    /** Valida que este gerente e o responsavel pela academia (base da RN04). */
    public void autorizarOperacaoNa(Academia academia, String operacao) {
        boolean ehGerenteDaUnidade = getId().equals(academia.getGerenteId())
                && getAcademiaId() != null && getAcademiaId().equals(academia.getId());
        if (!ehGerenteDaUnidade) {
            throw new AcessoNegadoException(operacao);
        }
    }

    public NivelAcesso getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(NivelAcesso nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public double getBonusGestao() {
        return bonusGestao;
    }

    public void setBonusGestao(double bonusGestao) {
        if (bonusGestao < 0) {
            throw new IllegalArgumentException("Bonus de gestao nao pode ser negativo.");
        }
        this.bonusGestao = bonusGestao;
    }

    /** Preenche os dados de contexto usados por {@link #gerarRelatorio()} e {@link #calcularComissao()}. */
    public void preencherDadosGestao(int funcionariosAtivos, int clientesAtivos,
                                     int clientesInadimplentes, double receitaAcademia) {
        this.funcionariosAtivos = funcionariosAtivos;
        this.clientesAtivos = clientesAtivos;
        this.clientesInadimplentes = clientesInadimplentes;
        this.receitaAcademia = receitaAcademia;
    }
}
