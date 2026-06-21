package br.com.redeacademia.exception;

/**
 * Excecao base de todas as regras de negocio do sistema.
 * Estende RuntimeException (excecao nao verificada) para nao poluir as
 * assinaturas com {@code throws}, mas e capturada de forma centralizada
 * pelo menu principal, que exibe a mensagem ao usuario sem encerrar o sistema.
 */
public class AcademiaException extends RuntimeException {

    public AcademiaException(String mensagem) {
        super(mensagem);
    }
}
