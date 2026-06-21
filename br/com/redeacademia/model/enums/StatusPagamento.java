package br.com.redeacademia.model.enums;

/**
 * Estados possiveis de um {@code Pagamento} (maquina de estados).
 */
public enum StatusPagamento {
    PENDENTE,
    PAGO,
    ATRASADO,
    CANCELADO
}
