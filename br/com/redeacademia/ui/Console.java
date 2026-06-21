package br.com.redeacademia.ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Utilitario de entrada/saida no terminal. Centraliza a leitura validada de
 * dados do usuario (texto, numeros, datas e enums), reapresentando o prompt
 * enquanto a entrada for invalida.
 */
public class Console {

    private final Scanner scanner = new Scanner(System.in);

    public void titulo(String texto) {
        System.out.println();
        System.out.println("==== " + texto + " ====");
    }

    public void msg(String texto) {
        System.out.println(texto);
    }

    public void erro(String texto) {
        System.out.println("[!] " + texto);
    }

    public void sucesso(String texto) {
        System.out.println("[OK] " + texto);
    }

    public String lerTexto(String prompt) {
        System.out.print(prompt + ": ");
        if (!scanner.hasNextLine()) {
            // Entrada encerrada (EOF / Ctrl+Z): finaliza de forma limpa.
            // O shutdown hook registrado no Main persiste o estado antes de sair.
            System.out.println("\n[Sistema] Entrada encerrada.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    public String lerTextoObrigatorio(String prompt) {
        while (true) {
            String valor = lerTexto(prompt);
            if (!valor.isBlank()) {
                return valor;
            }
            erro("Campo obrigatorio.");
        }
    }

    public int lerInt(String prompt) {
        while (true) {
            String valor = lerTexto(prompt);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                erro("Digite um numero inteiro valido.");
            }
        }
    }

    public double lerDouble(String prompt) {
        while (true) {
            String valor = lerTexto(prompt).replace(",", ".");
            try {
                return Double.parseDouble(valor);
            } catch (NumberFormatException e) {
                erro("Digite um numero valido (ex.: 99.90).");
            }
        }
    }

    public LocalDate lerData(String prompt) {
        while (true) {
            String valor = lerTexto(prompt + " (AAAA-MM-DD)");
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException e) {
                erro("Data invalida. Use o formato AAAA-MM-DD (ex.: 1995-07-20).");
            }
        }
    }

    /** Le um enum a partir de um menu numerado dos seus valores. */
    public <E extends Enum<E>> E lerEnum(String prompt, Class<E> tipo) {
        E[] valores = tipo.getEnumConstants();
        msg(prompt + ":");
        for (int i = 0; i < valores.length; i++) {
            msg("  " + (i + 1) + ") " + valores[i].name());
        }
        while (true) {
            int op = lerInt("Opcao");
            if (op >= 1 && op <= valores.length) {
                return valores[op - 1];
            }
            erro("Opcao fora do intervalo.");
        }
    }

    // -- Leitura para EDICAO: mostra o valor atual; Enter (vazio) mantem o valor --

    public String lerTextoOuManter(String campo, String atual) {
        String entrada = lerTexto(rotuloComAtual(campo, atual));
        return entrada.isBlank() ? atual : entrada;
    }

    public double lerDoubleOuManter(String campo, double atual) {
        while (true) {
            String entrada = lerTexto(rotuloComAtual(campo, String.valueOf(atual))).replace(",", ".");
            if (entrada.isBlank()) {
                return atual;
            }
            try {
                return Double.parseDouble(entrada);
            } catch (NumberFormatException e) {
                erro("Digite um numero valido (ex.: 99.90).");
            }
        }
    }

    public int lerIntOuManter(String campo, int atual) {
        while (true) {
            String entrada = lerTexto(rotuloComAtual(campo, String.valueOf(atual)));
            if (entrada.isBlank()) {
                return atual;
            }
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                erro("Digite um numero inteiro valido.");
            }
        }
    }

    public <E extends Enum<E>> E lerEnumOuManter(String campo, Class<E> tipo, E atual) {
        E[] valores = tipo.getEnumConstants();
        msg(rotuloComAtual(campo, atual == null ? null : atual.name()) + ":");
        for (int i = 0; i < valores.length; i++) {
            msg("  " + (i + 1) + ") " + valores[i].name());
        }
        while (true) {
            String entrada = lerTexto("Opcao");
            if (entrada.isBlank()) {
                return atual;
            }
            try {
                int op = Integer.parseInt(entrada);
                if (op >= 1 && op <= valores.length) {
                    return valores[op - 1];
                }
            } catch (NumberFormatException ignored) {
                // cai no erro abaixo
            }
            erro("Opcao invalida.");
        }
    }

    /** Monta "Campo [valor atual]" (ou so "Campo" se nao houver valor atual). */
    private String rotuloComAtual(String campo, String atual) {
        return (atual == null || atual.isBlank()) ? campo : campo + " [" + atual + "]";
    }

    public boolean confirmar(String prompt) {
        String valor = lerTexto(prompt + " (s/n)").toLowerCase();
        return valor.startsWith("s");
    }

    public void pausar() {
        System.out.print("\n<Enter> para continuar...");
        scanner.nextLine();
    }
}
