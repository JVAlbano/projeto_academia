package br.com.redeacademia.repository;

import br.com.redeacademia.model.enums.NivelAcesso;
import br.com.redeacademia.model.enums.TurnoFuncionario;
import br.com.redeacademia.model.pessoa.Funcionario;
import br.com.redeacademia.model.pessoa.Gerente;
import br.com.redeacademia.model.pessoa.Instrutor;
import br.com.redeacademia.model.pessoa.Recepcionista;
import br.com.redeacademia.util.MapUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Persistencia de funcionarios (funcionarios.json) com SERIALIZACAO POLIMORFICA:
 * o campo "tipo" ("Gerente" | "Instrutor" | "Recepcionista") permite reconstruir
 * a subclasse correta na desserializacao.
 */
public class FuncionarioRepository extends Repositorio<Funcionario> {

    public FuncionarioRepository() {
        super("funcionarios.txt");
    }

    /** Funcionarios vinculados a uma academia. */
    public List<Funcionario> listarPorAcademia(String academiaId) {
        return itens.stream()
                .filter(f -> academiaId.equals(f.getAcademiaId()))
                .collect(Collectors.toList());
    }

    @Override
    protected String getId(Funcionario f) {
        return f.getId();
    }

    @Override
    protected Map<String, Object> paraMapa(Funcionario f) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tipo", f.getTipo());
        m.put("id", f.getId());
        m.put("nome", f.getNome());
        m.put("cpf", f.getCpf());
        m.put("email", f.getEmail());
        m.put("telefone", f.getTelefone());
        m.put("matriculaFunc", f.getMatriculaFunc());
        m.put("salario", f.getSalario());
        m.put("academiaId", f.getAcademiaId());

        if (f instanceof Gerente g) {
            m.put("nivelAcesso", g.getNivelAcesso() == null ? null : g.getNivelAcesso().name());
            m.put("bonusGestao", g.getBonusGestao());
        } else if (f instanceof Instrutor i) {
            m.put("cref", i.getCref());
            m.put("especialidade", i.getEspecialidade());
            m.put("cargaHoraria", i.getCargaHoraria());
        } else if (f instanceof Recepcionista r) {
            m.put("turno", r.getTurno() == null ? null : r.getTurno().name());
            m.put("senhaAcesso", r.getSenhaAcesso());
        }
        return m;
    }

    @Override
    protected Funcionario deMapa(Map<String, Object> m) {
        String tipo = MapUtil.str(m, "tipo");
        String id = MapUtil.str(m, "id");
        String nome = MapUtil.str(m, "nome");
        String cpf = MapUtil.str(m, "cpf");
        String email = MapUtil.str(m, "email");
        String telefone = MapUtil.str(m, "telefone");
        String matricula = MapUtil.str(m, "matriculaFunc");
        double salario = MapUtil.dbl(m, "salario");
        String academiaId = MapUtil.str(m, "academiaId");

        return switch (tipo) {
            case "Gerente" -> new Gerente(id, nome, cpf, email, telefone, matricula, salario, academiaId,
                    MapUtil.enumDe(m, "nivelAcesso", NivelAcesso.class), MapUtil.dbl(m, "bonusGestao"));
            case "Instrutor" -> new Instrutor(id, nome, cpf, email, telefone, matricula, salario, academiaId,
                    MapUtil.str(m, "cref"), MapUtil.str(m, "especialidade"), MapUtil.inteiro(m, "cargaHoraria"));
            case "Recepcionista" -> new Recepcionista(id, nome, cpf, email, telefone, matricula, salario, academiaId,
                    MapUtil.enumDe(m, "turno", TurnoFuncionario.class), MapUtil.str(m, "senhaAcesso"));
            default -> throw new IllegalStateException("Tipo de funcionario desconhecido: " + tipo);
        };
    }
}
