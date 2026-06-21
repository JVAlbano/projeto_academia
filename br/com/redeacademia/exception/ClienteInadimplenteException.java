package br.com.redeacademia.exception;

/** Lancada quando um cliente com pagamento ATRASADO tenta acessar ou receber treino (RN06). */
public class ClienteInadimplenteException extends AcademiaException {

    public ClienteInadimplenteException(String nomeCliente) {
        super("Operacao bloqueada: o cliente '" + nomeCliente + "' possui pagamento em atraso.");
    }
}
