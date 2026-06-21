package br.com.redeacademia.exception;

/** Lancada ao tentar ativar uma matricula sem um pagamento confirmado (RN05). */
public class MatriculaSemPagamentoException extends AcademiaException {

    public MatriculaSemPagamentoException() {
        super("A matricula nao pode ser ativada sem um pagamento com status PAGO.");
    }
}
