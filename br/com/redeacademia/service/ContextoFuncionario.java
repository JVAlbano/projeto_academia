package br.com.redeacademia.service;

import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.model.pessoa.Funcionario;
import br.com.redeacademia.model.pessoa.Gerente;
import br.com.redeacademia.model.pessoa.Instrutor;
import br.com.redeacademia.model.pessoa.Recepcionista;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Preenche, a partir do estado atual do sistema, os dados de contexto que as
 * subclasses de {@link Funcionario} precisam para {@code calcularComissao()} e
 * {@code gerarRelatorio()}. Mantem as entidades de dominio livres de dependencia
 * dos repositorios.
 */
public class ContextoFuncionario {

    private final Dados dados;

    public ContextoFuncionario(Dados dados) {
        this.dados = dados;
    }

    /** Preenche os dados de contexto adequados ao tipo concreto do funcionario. */
    public void preparar(Funcionario f) {
        if (f instanceof Gerente g) {
            prepararGerente(g);
        } else if (f instanceof Instrutor i) {
            prepararInstrutor(i);
        } else if (f instanceof Recepcionista r) {
            prepararRecepcionista(r);
        }
    }

    private void prepararGerente(Gerente g) {
        String aid = g.getAcademiaId();
        int funcionariosAtivos = dados.funcionarios().listarPorAcademia(aid).size();

        List<Matricula> ativas = dados.matriculas().listarPorAcademia(aid).stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                .collect(Collectors.toList());

        int clientesAtivos = (int) ativas.stream().map(Matricula::getClienteId).distinct().count();
        double receita = ativas.stream().mapToDouble(m -> Consultas.valorPlano(dados, m.getPlanoId())).sum();
        int inadimplentes = (int) ativas.stream()
                .map(Matricula::getClienteId)
                .distinct()
                .filter(id -> Consultas.clienteInadimplente(dados, id))
                .count();

        g.preencherDadosGestao(funcionariosAtivos, clientesAtivos, inadimplentes, receita);
    }

    private void prepararInstrutor(Instrutor i) {
        List<Treino> treinos = dados.treinos().listarPorInstrutor(i.getId());
        Set<String> alunos = treinos.stream().map(Treino::getClienteId).collect(Collectors.toSet());

        double somaPlanos = alunos.stream().mapToDouble(this::valorPlanoAtivoDoCliente).sum();
        double cargaMedia = treinos.isEmpty() ? 0.0
                : treinos.stream().mapToDouble(Treino::calcularCargaTotal).average().orElse(0.0);

        i.preencherDadosAcompanhamento(somaPlanos, alunos.size(), treinos.size(), cargaMedia);
    }

    private void prepararRecepcionista(Recepcionista r) {
        var acessos = dados.acessos().listarPorAcademia(r.getAcademiaId());
        int entradas = (int) acessos.stream()
                .filter(a -> a.getTipo() != null && a.getTipo().name().equals("ENTRADA")).count();
        int saidas = acessos.size() - entradas;

        Map<Integer, Long> porHora = acessos.stream()
                .filter(a -> a.getDataHora() != null)
                .collect(Collectors.groupingBy(a -> a.getDataHora().getHour(), Collectors.counting()));
        String pico = porHora.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> String.format("%02dh (%d acessos)", e.getKey(), e.getValue()))
                .orElse("-");

        r.preencherDadosAcessos(entradas, saidas, pico);
    }

    // ----------------------------------------------------------- auxiliares

    private double valorPlanoAtivoDoCliente(String clienteId) {
        return dados.matriculas().listarPorCliente(clienteId).stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                .mapToDouble(m -> Consultas.valorPlano(dados, m.getPlanoId()))
                .sum();
    }
}
