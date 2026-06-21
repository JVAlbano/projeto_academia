package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Pagamento;
import br.com.redeacademia.model.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Gestao de pagamentos: quitacao, cancelamento e consulta de inadimplentes. */
public class PagamentoService {

    private final Dados dados;

    public PagamentoService(Dados dados) {
        this.dados = dados;
    }

    public void registrarPagamento(String pagamentoId) {
        buscar(pagamentoId).pagar(LocalDate.now());
    }

    public void cancelarPagamento(String pagamentoId) {
        buscar(pagamentoId).cancelar();
    }

    public List<Pagamento> listarPorMatricula(String matriculaId) {
        return dados.pagamentos().listarPorMatricula(matriculaId);
    }

    /** Lista os pagamentos em atraso (inadimplentes) de uma academia. */
    public String listarInadimplentes(String academiaId) {
        List<String> matriculasDaAcademia = dados.matriculas().listarPorAcademia(academiaId).stream()
                .map(Matricula::getId)
                .collect(Collectors.toList());

        List<Pagamento> atrasados = dados.pagamentos().listar().stream()
                .filter(p -> p.getStatus() == StatusPagamento.ATRASADO)
                .filter(p -> matriculasDaAcademia.contains(p.getMatriculaId()))
                .collect(Collectors.toList());

        if (atrasados.isEmpty()) {
            return "Nenhum inadimplente nesta academia.";
        }
        StringBuilder sb = new StringBuilder("===== INADIMPLENTES =====\n");
        for (Pagamento p : atrasados) {
            String cliente = nomeClienteDaMatricula(p.getMatriculaId());
            sb.append(String.format("- %-20s | venc. %s | R$ %,.2f%n",
                    cliente, p.getDataVencimento(), p.getValor()));
        }
        return sb.toString().strip();
    }

    /** RN07 (parte): marca como ATRASADO todo pagamento PENDENTE vencido. @return quantos. */
    public int marcarPagamentosVencidos(LocalDate referencia) {
        int total = 0;
        for (Pagamento p : dados.pagamentos().listar()) {
            StatusPagamento antes = p.getStatus();
            p.verificarAtraso(referencia);
            if (antes != p.getStatus()) {
                total++;
            }
        }
        return total;
    }

    private String nomeClienteDaMatricula(String matriculaId) {
        return dados.matriculas().buscarPorId(matriculaId)
                .flatMap(m -> dados.clientes().buscarPorId(m.getClienteId()))
                .map(c -> c.getNome())
                .orElse("(cliente desconhecido)");
    }

    public Pagamento buscar(String pagamentoId) {
        return dados.pagamentos().buscarPorId(pagamentoId)
                .orElseThrow(() -> new AcademiaException("Pagamento nao encontrado: " + pagamentoId));
    }
}
