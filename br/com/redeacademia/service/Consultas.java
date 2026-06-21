package br.com.redeacademia.service;

import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Plano;
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
}
