package br.com.redeacademia.exception;

/** Lancada quando se tenta cadastrar um CPF ja existente no sistema (RN01). */
public class CpfDuplicadoException extends AcademiaException {

    public CpfDuplicadoException(String cpf) {
        super("Ja existe uma pessoa cadastrada com o CPF " + cpf + ".");
    }
}
