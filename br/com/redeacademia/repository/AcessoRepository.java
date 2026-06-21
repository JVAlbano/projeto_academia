package br.com.redeacademia.repository;

import br.com.redeacademia.model.AcessoRegistro;
import br.com.redeacademia.model.enums.TipoAcesso;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Persistencia dos registros de acesso (acessos.json). */
public class AcessoRepository extends Repositorio<AcessoRegistro> {

    public AcessoRepository() {
        super("acessos.txt");
    }

    public List<AcessoRegistro> listarPorAcademia(String academiaId) {
        return itens.stream()
                .filter(a -> academiaId.equals(a.getAcademiaId()))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(AcessoRegistro a) {
        return a.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(AcessoRegistro a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", a.getId());
        m.put("clienteId", a.getClienteId());
        m.put("academiaId", a.getAcademiaId());
        m.put("dataHora", a.getDataHora() == null ? null : a.getDataHora().toString());
        m.put("tipo", a.getTipo() == null ? null : a.getTipo().name());
        return m;
    }

    @Override
    protected AcessoRegistro deMapa(Map<String, Object> m) {
        return new AcessoRegistro(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "clienteId"),
                MapUtil.str(m, "academiaId"),
                MapUtil.dataHora(m, "dataHora"),
                MapUtil.enumDe(m, "tipo", TipoAcesso.class));
    }
}
