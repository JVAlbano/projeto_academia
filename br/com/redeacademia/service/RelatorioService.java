package br.com.redeacademia.service;

import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.enums.StatusMatricula;

import java.util.List;
import java.util.stream.Collectors;

/** Relatorio gerencial consolidado de toda a rede (raiz da hierarquia). */
public class RelatorioService {

    private final Dados dados;

    public RelatorioService(Dados dados) {
        this.dados = dados;
    }

    public String relatorioConsolidado() {
        if (!dados.possuiRede()) {
            return "Nenhuma rede cadastrada.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("================ RELATORIO CONSOLIDADO DA REDE ================\n");
        sb.append("Rede: ").append(dados.getRede().getNome())
                .append(" | CNPJ: ").append(dados.getRede().getCnpj()).append("\n");
        sb.append("Academias: ").append(dados.getRede().quantidadeAcademias()).append("\n");
        sb.append("--------------------------------------------------------------\n");

        double receitaTotal = 0;
        int totalClientesAtivos = 0;
        int totalInadimplentes = 0;

        for (Academia a : dados.getRede().listarAcademias()) {
            List<Matricula> ativas = dados.matriculas().listarPorAcademia(a.getId()).stream()
                    .filter(m -> m.getStatus() == StatusMatricula.ATIVA)
                    .collect(Collectors.toList());

            int funcionarios = dados.funcionarios().listarPorAcademia(a.getId()).size();
            int clientesAtivos = (int) ativas.stream().map(Matricula::getClienteId).distinct().count();
            double receita = ativas.stream().mapToDouble(m -> Consultas.valorPlano(dados, m.getPlanoId())).sum();
            int inadimplentes = (int) ativas.stream()
                    .map(Matricula::getClienteId)
                    .distinct()
                    .filter(id -> Consultas.clienteInadimplente(dados, id))
                    .count();

            receitaTotal += receita;
            totalClientesAtivos += clientesAtivos;
            totalInadimplentes += inadimplentes;

            sb.append(String.format("# %s%n", a.getNome()));
            sb.append(String.format("   Funcionarios: %d | Clientes ativos: %d | Inadimplentes: %d | Receita: R$ %,.2f%n",
                    funcionarios, clientesAtivos, inadimplentes, receita));
        }

        sb.append("--------------------------------------------------------------\n");
        sb.append(String.format("TOTAL -> Clientes ativos: %d | Inadimplentes: %d | Receita mensal: R$ %,.2f",
                totalClientesAtivos, totalInadimplentes, receitaTotal));
        return sb.toString();
    }
}
