package br.com.redeacademia.exception;

/** Lancada ao executar uma operacao que exige matricula ATIVA sobre uma matricula em outro estado. */
public class MatriculaInativaException extends AcademiaException {

    public MatriculaInativaException(String statusAtual) {
        super("Operacao indisponivel: a matricula esta " + statusAtual + " (esperado: ATIVA).");
    }
}
