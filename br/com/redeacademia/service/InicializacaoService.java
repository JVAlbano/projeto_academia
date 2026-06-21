package br.com.redeacademia.service;

import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.enums.StatusMatricula;

import java.time.LocalDate;

/**
 * Aplica as regras temporais automaticas na inicializacao do sistema (RN07):
 * pagamentos pendentes vencidos viram ATRASADO e matriculas ativas vencidas viram VENCIDA.
 */
public class InicializacaoService {

    private final Dados dados;
    private final PagamentoService pagamentoService;

    public InicializacaoService(Dados dados) {
        this.dados = dados;
        this.pagamentoService = new PagamentoService(dados);
    }

    /** @return mensagem resumindo as transicoes automaticas aplicadas. */
    public String aplicarRegrasTemporais() {
        LocalDate hoje = LocalDate.now();

        int pagamentosAtrasados = pagamentoService.marcarPagamentosVencidos(hoje);

        int matriculasVencidas = 0;
        for (Matricula m : dados.matriculas().listar()) {
            if (m.getStatus() == StatusMatricula.ATIVA && m.estaVencida(hoje)) {
                m.marcarVencida();
                matriculasVencidas++;
            }
        }
        return String.format("RN07 aplicada: %d pagamento(s) -> ATRASADO, %d matricula(s) -> VENCIDA.",
                pagamentosAtrasados, matriculasVencidas);
    }
}
