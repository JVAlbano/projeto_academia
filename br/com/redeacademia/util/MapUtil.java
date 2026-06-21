package br.com.redeacademia.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Helpers para ler valores tipados de um {@code Map<String,Object>} produzido pelo
 * parser {@link Json} (que representa todo numero como {@link Double}). Tratam null
 * com seguranca para a desserializacao das entidades.
 */
public final class MapUtil {

    private MapUtil() {
    }

    public static String str(Map<String, Object> m, String chave) {
        Object v = m.get(chave);
        return v == null ? null : v.toString();
    }

    public static double dbl(Map<String, Object> m, String chave) {
        Object v = m.get(chave);
        return v == null ? 0.0 : ((Number) v).doubleValue();
    }

    public static int inteiro(Map<String, Object> m, String chave) {
        Object v = m.get(chave);
        return v == null ? 0 : ((Number) v).intValue();
    }

    public static boolean bool(Map<String, Object> m, String chave) {
        Object v = m.get(chave);
        return v != null && (Boolean) v;
    }

    public static LocalDate data(Map<String, Object> m, String chave) {
        String v = str(m, chave);
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
    }

    public static LocalDateTime dataHora(Map<String, Object> m, String chave) {
        String v = str(m, chave);
        return (v == null || v.isBlank()) ? null : LocalDateTime.parse(v);
    }

    public static <E extends Enum<E>> E enumDe(Map<String, Object> m, String chave, Class<E> tipo) {
        String v = str(m, chave);
        return (v == null || v.isBlank()) ? null : Enum.valueOf(tipo, v);
    }
}
