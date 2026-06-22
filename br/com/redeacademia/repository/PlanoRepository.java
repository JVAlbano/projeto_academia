package br.com.redeacademia.repository;

import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.enums.AbrangenciaPlano;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Persistencia dos planos (planos.json), agrupaveis por academia. */
public class PlanoRepository extends Repositorio<Plano> {

    public PlanoRepository() {
        super("planos.txt");
    }

    /** Planos da academia informada, incluindo os planos de abrangencia REDE (disponiveis em qualquer unidade). */
    public List<Plano> listarPorAcademia(String academiaId) {
        return itens.stream()
                .filter(p -> p.cobreAcademia(academiaId))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(Plano p) {
        return p.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Plano p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nome", p.getNome());
        m.put("valorMensal", p.getValorMensal());
        m.put("duracaoMeses", p.getDuracaoMeses());
        m.put("ativo", p.isAtivo());
        m.put("academiaId", p.getAcademiaId());
        m.put("abrangencia", p.getAbrangencia().name());
        return m;
    }

    @Override
    protected Plano deMapa(Map<String, Object> m) {
        AbrangenciaPlano abrangencia = MapUtil.enumDe(m, "abrangencia", AbrangenciaPlano.class);
        Plano p = new Plano(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "nome"),
                MapUtil.dbl(m, "valorMensal"),
                MapUtil.inteiro(m, "duracaoMeses"),
                MapUtil.str(m, "academiaId"),
                abrangencia == null ? AbrangenciaPlano.ACADEMIA : abrangencia);
        p.setAtivo(MapUtil.bool(m, "ativo"));
        return p;
    }
}
