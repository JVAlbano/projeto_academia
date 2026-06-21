package br.com.redeacademia.repository;

import br.com.redeacademia.model.Exercicio;
import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.NivelTreino;
import br.com.redeacademia.util.MapUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Persistencia dos treinos (treinos.json). Os exercicios sao serializados de forma
 * ANINHADA dentro de cada treino (composicao Treino 1--* Exercicio).
 */
public class TreinoRepository extends Repositorio<Treino> {

    public TreinoRepository() {
        super("treinos.txt");
    }

    public List<Treino> listarPorCliente(String clienteId) {
        return itens.stream()
                .filter(t -> clienteId.equals(t.getClienteId()))
                .collect(Collectors.toList());
    }

    public List<Treino> listarPorInstrutor(String instrutorId) {
        return itens.stream()
                .filter(t -> instrutorId.equals(t.getInstrutorId()))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(Treino t) {
        return t.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Treino t) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", t.getId());
        m.put("nome", t.getNome());
        m.put("objetivo", t.getObjetivo());
        m.put("nivel", t.getNivel() == null ? null : t.getNivel().name());
        m.put("duracaoMinutos", t.getDuracaoMinutos());
        m.put("clienteId", t.getClienteId());
        m.put("instrutorId", t.getInstrutorId());

        List<Object> exercicios = new ArrayList<>();
        for (Exercicio e : t.getExercicios()) {
            Map<String, Object> em = new LinkedHashMap<>();
            em.put("nome", e.getNome());
            em.put("grupoMuscular", e.getGrupoMuscular());
            em.put("series", e.getSeries());
            em.put("repeticoes", e.getRepeticoes());
            em.put("carga", e.getCarga());
            exercicios.add(em);
        }
        m.put("exercicios", exercicios);
        return m;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Treino deMapa(Map<String, Object> m) {
        Treino t = new Treino(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "nome"),
                MapUtil.str(m, "objetivo"),
                MapUtil.enumDe(m, "nivel", NivelTreino.class),
                MapUtil.inteiro(m, "duracaoMinutos"),
                MapUtil.str(m, "clienteId"),
                MapUtil.str(m, "instrutorId"));

        Object exercicios = m.get("exercicios");
        if (exercicios instanceof List<?> lista) {
            for (Object item : lista) {
                Map<String, Object> em = (Map<String, Object>) item;
                t.adicionarExercicio(new Exercicio(
                        MapUtil.str(em, "nome"),
                        MapUtil.str(em, "grupoMuscular"),
                        MapUtil.inteiro(em, "series"),
                        MapUtil.inteiro(em, "repeticoes"),
                        MapUtil.dbl(em, "carga")));
            }
        }
        return t;
    }
}
