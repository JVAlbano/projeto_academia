package br.com.redeacademia.exception;

/** Lancada ao tentar atribuir um segundo gerente a uma academia (RN03). */
public class GerenteJaAtribuidoException extends AcademiaException {

    public GerenteJaAtribuidoException(String nomeAcademia) {
        super("A academia '" + nomeAcademia + "' ja possui um gerente atribuido.");
    }
}
