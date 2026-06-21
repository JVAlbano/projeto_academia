package br.com.redeacademia.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mini biblioteca JSON escrita do zero (sem dependencias externas).
 *
 * <p>O parser converte texto JSON na seguinte arvore de objetos Java:</p>
 * <ul>
 *   <li>objeto  -&gt; {@link LinkedHashMap}&lt;String, Object&gt; (preserva a ordem das chaves)</li>
 *   <li>array   -&gt; {@link ArrayList}&lt;Object&gt;</li>
 *   <li>string  -&gt; {@link String}</li>
 *   <li>numero  -&gt; {@link Double}</li>
 *   <li>boolean -&gt; {@link Boolean}</li>
 *   <li>null    -&gt; {@code null}</li>
 * </ul>
 *
 * <p>O serializador faz o caminho inverso, gerando JSON identado.</p>
 */
public final class Json {

    private Json() {
    }

    // ---------------------------------------------------------------- Parsing

    /** Converte texto JSON em uma arvore de Map/List/String/Double/Boolean/null. */
    public static Object parse(String texto) {
        return new Parser(texto).parseDocumento();
    }

    /** Atalho para quando o JSON de topo e um array de objetos. */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseListaDeObjetos(String texto) {
        Object raiz = parse(texto);
        List<Map<String, Object>> resultado = new ArrayList<>();
        if (raiz instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> mapa) {
                    resultado.add((Map<String, Object>) mapa);
                }
            }
        }
        return resultado;
    }

    private static final class Parser {
        private final String src;
        private int pos;

        Parser(String src) {
            this.src = src;
            this.pos = 0;
        }

        Object parseDocumento() {
            pularEspacos();
            if (pos >= src.length()) {
                return null;
            }
            Object valor = parseValor();
            pularEspacos();
            return valor;
        }

        private Object parseValor() {
            pularEspacos();
            char c = atual();
            return switch (c) {
                case '{' -> parseObjeto();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't', 'f' -> parseBoolean();
                case 'n' -> parseNull();
                default -> parseNumero();
            };
        }

        private Map<String, Object> parseObjeto() {
            Map<String, Object> mapa = new LinkedHashMap<>();
            consumir('{');
            pularEspacos();
            if (atual() == '}') {
                consumir('}');
                return mapa;
            }
            while (true) {
                pularEspacos();
                String chave = parseString();
                pularEspacos();
                consumir(':');
                Object valor = parseValor();
                mapa.put(chave, valor);
                pularEspacos();
                char c = atual();
                if (c == ',') {
                    consumir(',');
                } else if (c == '}') {
                    consumir('}');
                    break;
                } else {
                    throw erro("esperado ',' ou '}'");
                }
            }
            return mapa;
        }

        private List<Object> parseArray() {
            List<Object> lista = new ArrayList<>();
            consumir('[');
            pularEspacos();
            if (atual() == ']') {
                consumir(']');
                return lista;
            }
            while (true) {
                lista.add(parseValor());
                pularEspacos();
                char c = atual();
                if (c == ',') {
                    consumir(',');
                } else if (c == ']') {
                    consumir(']');
                    break;
                } else {
                    throw erro("esperado ',' ou ']'");
                }
            }
            return lista;
        }

        private String parseString() {
            consumir('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = src.charAt(pos++);
                if (c == '"') {
                    break;
                }
                if (c == '\\') {
                    char esc = src.charAt(pos++);
                    switch (esc) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'n' -> sb.append('\n');
                        case 't' -> sb.append('\t');
                        case 'r' -> sb.append('\r');
                        case 'b' -> sb.append('\b');
                        case 'f' -> sb.append('\f');
                        case 'u' -> {
                            String hex = src.substring(pos, pos + 4);
                            sb.append((char) Integer.parseInt(hex, 16));
                            pos += 4;
                        }
                        default -> throw erro("escape invalido: \\" + esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        private Boolean parseBoolean() {
            if (src.startsWith("true", pos)) {
                pos += 4;
                return Boolean.TRUE;
            }
            if (src.startsWith("false", pos)) {
                pos += 5;
                return Boolean.FALSE;
            }
            throw erro("valor booleano invalido");
        }

        private Object parseNull() {
            if (src.startsWith("null", pos)) {
                pos += 4;
                return null;
            }
            throw erro("valor invalido");
        }

        private Double parseNumero() {
            int inicio = pos;
            while (pos < src.length() && "+-0123456789.eE".indexOf(src.charAt(pos)) >= 0) {
                pos++;
            }
            String numero = src.substring(inicio, pos);
            if (numero.isEmpty()) {
                throw erro("numero invalido");
            }
            return Double.parseDouble(numero);
        }

        private char atual() {
            if (pos >= src.length()) {
                throw erro("fim inesperado do JSON");
            }
            return src.charAt(pos);
        }

        private void consumir(char esperado) {
            if (atual() != esperado) {
                throw erro("esperado '" + esperado + "'");
            }
            pos++;
        }

        private void pularEspacos() {
            while (pos < src.length() && Character.isWhitespace(src.charAt(pos))) {
                pos++;
            }
        }

        private IllegalArgumentException erro(String msg) {
            return new IllegalArgumentException("JSON invalido na posicao " + pos + ": " + msg);
        }
    }

    // ------------------------------------------------------------ Serializacao

    /** Serializa uma arvore de Map/List/valores em JSON identado. */
    public static String escrever(Object valor) {
        StringBuilder sb = new StringBuilder();
        escreverValor(valor, sb, 0);
        return sb.toString();
    }

    private static void escreverValor(Object valor, StringBuilder sb, int nivel) {
        if (valor == null) {
            sb.append("null");
        } else if (valor instanceof Map<?, ?> mapa) {
            escreverObjeto(mapa, sb, nivel);
        } else if (valor instanceof List<?> lista) {
            escreverArray(lista, sb, nivel);
        } else if (valor instanceof String texto) {
            escreverString(texto, sb);
        } else if (valor instanceof Boolean || valor instanceof Number) {
            sb.append(valor.toString());
        } else {
            // Qualquer outro tipo (enum, LocalDate, etc.) e serializado como string.
            escreverString(valor.toString(), sb);
        }
    }

    private static void escreverObjeto(Map<?, ?> mapa, StringBuilder sb, int nivel) {
        if (mapa.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int i = 0;
        for (Map.Entry<?, ?> entrada : mapa.entrySet()) {
            identar(sb, nivel + 1);
            escreverString(String.valueOf(entrada.getKey()), sb);
            sb.append(": ");
            escreverValor(entrada.getValue(), sb, nivel + 1);
            if (++i < mapa.size()) {
                sb.append(',');
            }
            sb.append('\n');
        }
        identar(sb, nivel);
        sb.append('}');
    }

    private static void escreverArray(List<?> lista, StringBuilder sb, int nivel) {
        if (lista.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            identar(sb, nivel + 1);
            escreverValor(lista.get(i), sb, nivel + 1);
            if (i < lista.size() - 1) {
                sb.append(',');
            }
            sb.append('\n');
        }
        identar(sb, nivel);
        sb.append(']');
    }

    private static void escreverString(String texto, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\t' -> sb.append("\\t");
                case '\r' -> sb.append("\\r");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                default -> sb.append(c);
            }
        }
        sb.append('"');
    }

    private static void identar(StringBuilder sb, int nivel) {
        sb.append("  ".repeat(nivel));
    }
}
