package br.com.redeacademia.exception;

/** Lancada quando um cliente tenta aderir a um plano de academia diferente da sua (RN02). */
public class PlanoDeOutraAcademiaException extends AcademiaException {

    public PlanoDeOutraAcademiaException() {
        super("O cliente so pode contratar planos da academia em que esta matriculado.");
    }
}
