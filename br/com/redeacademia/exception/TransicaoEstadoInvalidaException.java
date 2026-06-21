package br.com.redeacademia.exception;

/** Lancada quando se tenta uma transicao de estado nao permitida em uma maquina de estados. */
public class TransicaoEstadoInvalidaException extends AcademiaException {

    public TransicaoEstadoInvalidaException(String entidade, Object de, Object para) {
        super("Transicao de estado invalida em " + entidade + ": " + de + " -> " + para + ".");
    }
}
