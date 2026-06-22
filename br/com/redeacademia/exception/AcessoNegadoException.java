package br.com.redeacademia.exception;

/**
 * Lancada quando um funcionario sem perfil de gerente tenta uma operacao administrativa (RN04)
 * ou quando um cliente tenta acessar uma academia que seu plano nao cobre (RN08).
 */
public class AcessoNegadoException extends AcademiaException {

    /** Operacao administrativa restrita ao gerente da academia (RN04). */
    public AcessoNegadoException(String operacao) {
        this(operacao, true);
    }

    private AcessoNegadoException(String valor, boolean operacaoDeGerente) {
        super(operacaoDeGerente
                ? "Acesso negado: apenas o gerente da academia pode " + valor + "."
                : "Acesso negado: " + valor + " nao possui matricula ativa que cubra esta academia.");
    }

    /** Cliente sem matricula ativa que cubra a academia onde tenta entrar (RN08). */
    public static AcessoNegadoException semCobertura(String nomeCliente) {
        return new AcessoNegadoException(nomeCliente, false);
    }
}
