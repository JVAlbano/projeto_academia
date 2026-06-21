package br.com.redeacademia.repository;

import br.com.redeacademia.model.Academia;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/** Persistencia das academias (academias.json). */
public class AcademiaRepository extends Repositorio<Academia> {

    public AcademiaRepository() {
        super("academias.txt");
    }

    @Override
    protected String getId(Academia a) {
        return a.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Academia a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("nome", a.getNome());
        m.put("endereco", a.getEndereco());
        m.put("telefone", a.getTelefone());
        m.put("capacidadeMaxima", a.getCapacidadeMaxima());
        m.put("redeId", a.getRedeId());
        m.put("gerenteId", a.getGerenteId());
        m.put("ativa", a.isAtiva());
        return m;
    }

    @Override
    protected Academia deMapa(Map<String, Object> m) {
        Academia a = new Academia(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "nome"),
                MapUtil.str(m, "endereco"),
                MapUtil.str(m, "telefone"),
                MapUtil.inteiro(m, "capacidadeMaxima"),
                MapUtil.str(m, "redeId"));
        String gerenteId = MapUtil.str(m, "gerenteId");
        if (gerenteId != null && !gerenteId.isBlank()) {
            a.atribuirGerente(gerenteId);
        }
        a.setAtiva(MapUtil.bool(m, "ativa"));
        return a;
    }
}
