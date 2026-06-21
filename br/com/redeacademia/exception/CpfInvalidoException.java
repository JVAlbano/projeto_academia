package br.com.redeacademia.exception;

/** Lancada quando um CPF possui formato ou digitos verificadores invalidos. */
public class CpfInvalidoException extends AcademiaException {

    public CpfInvalidoException(String cpf) {
        super("CPF invalido: '" + cpf + "'. Verifique o formato e os digitos verificadores.");
    }
}
