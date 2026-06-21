package br.com.redeacademia.repository;

import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Persistencia das matriculas (matriculas.json). */
public class MatriculaRepository extends Repositorio<Matricula> {

    public MatriculaRepository() {
        super("matriculas.txt");
    }

    public List<Matricula> listarPorAcademia(String academiaId) {
        return itens.stream()
                .filter(mat -> academiaId.equals(mat.getAcademiaId()))
                .collect(Collectors.toList());
    }

    public List<Matricula> listarPorCliente(String clienteId) {
        return itens.stream()
                .filter(mat -> clienteId.equals(mat.getClienteId()))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(Matricula mat) {
        return mat.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Matricula mat) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", mat.getId());
        m.put("clienteId", mat.getClienteId());
        m.put("planoId", mat.getPlanoId());
        m.put("academiaId", mat.getAcademiaId());
        m.put("status", mat.getStatus().name());
        m.put("dataInicio", mat.getDataInicio() == null ? null : mat.getDataInicio().toString());
        m.put("dataVencimento", mat.getDataVencimento() == null ? null : mat.getDataVencimento().toString());
        return m;
    }

    @Override
    protected Matricula deMapa(Map<String, Object> m) {
        Matricula mat = new Matricula(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "clienteId"),
                MapUtil.str(m, "planoId"),
                MapUtil.str(m, "academiaId"),
                MapUtil.data(m, "dataInicio"),
                MapUtil.data(m, "dataVencimento"));
        mat.restaurarStatus(MapUtil.enumDe(m, "status", StatusMatricula.class));
        return mat;
    }
}
