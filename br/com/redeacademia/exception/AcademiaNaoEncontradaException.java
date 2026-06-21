package br.com.redeacademia.exception;

/** Lancada quando uma busca por ID de academia nao retorna resultado. */
public class AcademiaNaoEncontradaException extends AcademiaException {

    public AcademiaNaoEncontradaException(String id) {
        super("Academia nao encontrada para o id: " + id + ".");
    }
}
