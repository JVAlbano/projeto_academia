package br.com.redeacademia.service;

import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.model.enums.StatusPagamento;

import java.util.Set;
import java.util.stream.Collectors;

/** Consultas de negocio reutilizadas por varios services (evita duplicacao). */
public final class Consultas {

    private Consultas() {
    }

    /** Valor mensal de um plano (0 se nao encontrado). */
    public static double valorPlano(Dados dados, String planoId) {
        return dados.planos().buscarPorId(planoId).map(Plano::getValorMensal).orElse(0.0);
    }

    /** RN06: um cliente esta inadimplente se possui algum pagamento ATRASADO. */
    public static boolean clienteInadimplente(Dados dados, String clienteId) {
        Set<String> matriculasDoCliente = dados.matriculas().listarPorCliente(clienteId).stream()
                .map(Matricula::getId)
                .collect(Collectors.toSet());
        return dados.pagamentos().listar().stream()
                .filter(p -> matriculasDoCliente.contains(p.getMatriculaId()))
                .anyMatch(p -> p.getStatus() == StatusPagamento.ATRASADO);
    }

    /** RN08: o cliente tem acesso na academia se tem matricula ATIVA cujo plano a cobre. */
    public static boolean clienteTemAcessoNa(Dados dados, String clienteId, String academiaId) {
        return dados.matriculas().listarPorCliente(clienteId).stream()
                .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                .map(m -> dados.planos().buscarPorId(m.getPlanoId()).orElse(null))
                .filter(p -> p != null)
                .anyMatch(p -> p.cobreAcademia(academiaId));
    }
}
