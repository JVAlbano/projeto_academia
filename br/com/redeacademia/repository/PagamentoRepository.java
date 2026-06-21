package br.com.redeacademia.repository;

import br.com.redeacademia.model.Pagamento;
import br.com.redeacademia.model.enums.StatusPagamento;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Persistencia dos pagamentos (pagamentos.json). */
public class PagamentoRepository extends Repositorio<Pagamento> {

    public PagamentoRepository() {
        super("pagamentos.txt");
    }

    public List<Pagamento> listarPorMatricula(String matriculaId) {
        return itens.stream()
                .filter(p -> matriculaId.equals(p.getMatriculaId()))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(Pagamento p) {
        return p.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Pagamento p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("matriculaId", p.getMatriculaId());
        m.put("valor", p.getValor());
        m.put("status", p.getStatus().name());
        m.put("dataVencimento", p.getDataVencimento() == null ? null : p.getDataVencimento().toString());
        m.put("dataPagamento", p.getDataPagamento() == null ? null : p.getDataPagamento().toString());
        return m;
    }

    @Override
    protected Pagamento deMapa(Map<String, Object> m) {
        Pagamento p = new Pagamento(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "matriculaId"),
                MapUtil.dbl(m, "valor"),
                MapUtil.data(m, "dataVencimento"));
        p.restaurarStatus(MapUtil.enumDe(m, "status", StatusPagamento.class));
        p.setDataPagamento(MapUtil.data(m, "dataPagamento"));
        return p;
    }
}
