package br.com.redeacademia.repository;

import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/** Persistencia de clientes (clientes.json). */
public class ClienteRepository extends Repositorio<Cliente> {

    public ClienteRepository() {
        super("clientes.txt");
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        return itens.stream()
                .filter(c -> c.getCpf().replaceAll("\\D", "").equals(cpf.replaceAll("\\D", "")))
                .findFirst();
    }

    @Override
    protected String getId(Cliente c) {
        return c.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Cliente c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("nome", c.getNome());
        m.put("cpf", c.getCpf());
        m.put("email", c.getEmail());
        m.put("telefone", c.getTelefone());
        m.put("dataNascimento", c.getDataNascimento() == null ? null : c.getDataNascimento().toString());
        m.put("objetivo", c.getObjetivo());
        m.put("peso", c.getPeso());
        m.put("altura", c.getAltura());
        m.put("ativo", c.verificarAtivo());
        m.put("academiaId", c.getAcademiaId());
        return m;
    }

    @Override
    protected Cliente deMapa(Map<String, Object> m) {
        Cliente c = new Cliente(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "nome"),
                MapUtil.str(m, "cpf"),
                MapUtil.str(m, "email"),
                MapUtil.str(m, "telefone"),
                MapUtil.data(m, "dataNascimento"),
                MapUtil.str(m, "objetivo"),
                MapUtil.dbl(m, "peso"),
                MapUtil.dbl(m, "altura"),
                MapUtil.str(m, "academiaId"));
        c.setAtivo(MapUtil.bool(m, "ativo"));
        return c;
    }
}
