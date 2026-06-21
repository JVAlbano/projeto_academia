package br.com.redeacademia.util;

import java.util.UUID;

/** Gera identificadores unicos curtos com prefixo legivel (ex.: "CLI-3f9a2b"). */
public final class GeradorId {

    private GeradorId() {
    }

    public static String gerar(String prefixo) {
        String sufixo = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        return prefixo + "-" + sufixo;
    }
}
