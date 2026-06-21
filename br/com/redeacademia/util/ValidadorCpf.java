package br.com.redeacademia.util;

import br.com.redeacademia.exception.CpfInvalidoException;

/**
 * Utilitario de validacao e normalizacao de CPF.
 * Implementa o algoritmo oficial dos digitos verificadores (modulo 11).
 */
public final class ValidadorCpf {

    private ValidadorCpf() {
    }

    /** Remove tudo que nao for digito. */
    public static String normalizar(String cpf) {
        if (cpf == null) {
            return "";
        }
        return cpf.replaceAll("\\D", "");
    }

    /** Formata um CPF de 11 digitos como 000.000.000-00. */
    public static String formatar(String cpf) {
        String n = normalizar(cpf);
        if (n.length() != 11) {
            return cpf;
        }
        return n.substring(0, 3) + "." + n.substring(3, 6) + "."
                + n.substring(6, 9) + "-" + n.substring(9, 11);
    }

    /** @return true se o CPF for valido (formato e digitos verificadores). */
    public static boolean isValido(String cpf) {
        String n = normalizar(cpf);
        if (n.length() != 11) {
            return false;
        }
        // Rejeita sequencias iguais (000..., 111..., etc.), que passam no calculo mas sao invalidas.
        if (n.chars().distinct().count() == 1) {
            return false;
        }
        int dig1 = calcularDigito(n, 9);
        int dig2 = calcularDigito(n, 10);
        return dig1 == (n.charAt(9) - '0') && dig2 == (n.charAt(10) - '0');
    }

    /**
     * Valida o CPF e lanca excecao se invalido.
     *
     * @throws CpfInvalidoException quando o CPF nao e valido.
     */
    public static void validar(String cpf) {
        if (!isValido(cpf)) {
            throw new CpfInvalidoException(cpf);
        }
    }

    /**
     * Gera um CPF valido a partir de uma base de 9 digitos, completando os digitos
     * verificadores. Util para criar dados de exemplo. @return CPF formatado.
     */
    public static String gerarValido(String base9digitos) {
        String b = normalizar(base9digitos);
        if (b.length() != 9) {
            throw new IllegalArgumentException("A base deve ter 9 digitos.");
        }
        int d1 = calcularDigito(b, 9);
        String com1 = b + d1;
        int d2 = calcularDigito(com1, 10);
        return formatar(b + d1 + d2);
    }

    private static int calcularDigito(String cpf, int qtdDigitos) {
        int soma = 0;
        int peso = qtdDigitos + 1;
        for (int i = 0; i < qtdDigitos; i++) {
            soma += (cpf.charAt(i) - '0') * peso;
            peso--;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
