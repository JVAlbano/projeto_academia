package br.com.redeacademia.exception;

/** Lancada quando um funcionario sem perfil de gerente tenta uma operacao administrativa (RN04). */
public class AcessoNegadoException extends AcademiaException {

    public AcessoNegadoException(String operacao) {
        super("Acesso negado: apenas o gerente da academia pode " + operacao + ".");
    }
}
