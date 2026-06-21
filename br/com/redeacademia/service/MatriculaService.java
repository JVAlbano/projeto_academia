package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.exception.MatriculaSemPagamentoException;
import br.com.redeacademia.exception.PlanoDeOutraAcademiaException;
import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Pagamento;
import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.model.enums.StatusPagamento;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.util.GeradorId;

import java.time.LocalDate;

/**
 * Orquestra matriculas e seus pagamentos - operacao que envolve varias classes
 * (Cliente, Plano, Matricula, Pagamento). Aplica RN02 e RN05.
 */
public class MatriculaService {

    private final Dados dados;

    public MatriculaService(Dados dados) {
        this.dados = dados;
    }

    /**
     * Cria uma matricula (estado inicial SUSPENSA) e ja gera o pagamento inicial PENDENTE.
     *
     * @throws PlanoDeOutraAcademiaException se o plano nao for da academia do cliente (RN02).
     */
    public Matricula criarMatricula(String clienteId, String planoId) {
        Cliente cliente = dados.clientes().buscarPorId(clienteId)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado: " + clienteId));
        Plano plano = dados.planos().buscarPorId(planoId)
                .orElseThrow(() -> new AcademiaException("Plano nao encontrado: " + planoId));

        // RN02: o plano precisa pertencer a academia do cliente.
        if (!plano.getAcademiaId().equals(cliente.getAcademiaId())) {
            throw new PlanoDeOutraAcademiaException();
        }

        LocalDate hoje = LocalDate.now();
        Matricula matricula = new Matricula(GeradorId.gerar("MAT"), clienteId, planoId,
                cliente.getAcademiaId(), hoje, hoje.plusMonths(plano.getDuracaoMeses()));
        dados.matriculas().adicionar(matricula);

        Pagamento pagamento = new Pagamento(GeradorId.gerar("PAG"), matricula.getId(),
                plano.getValorMensal(), hoje.plusDays(7));
        dados.pagamentos().adicionar(pagamento);

        return matricula;
    }

    /**
     * Ativa a matricula. Exige um pagamento com status PAGO vinculado (RN05).
     *
     * @throws MatriculaSemPagamentoException se nao houver pagamento confirmado.
     */
    public void ativarMatricula(String matriculaId) {
        Matricula matricula = buscar(matriculaId);
        boolean temPagamentoConfirmado = dados.pagamentos().listarPorMatricula(matriculaId).stream()
                .anyMatch(p -> p.getStatus() == StatusPagamento.PAGO);
        if (!temPagamentoConfirmado) {
            throw new MatriculaSemPagamentoException();
        }
        matricula.ativar();
    }

    public void suspenderMatricula(String matriculaId) {
        buscar(matriculaId).suspender();
    }

    public void cancelarMatricula(String matriculaId) {
        buscar(matriculaId).cancelar();
    }

    /** Renova uma matricula VENCIDA/SUSPENSA com um novo pagamento ja quitado. */
    public void renovarMatricula(String matriculaId) {
        Matricula matricula = buscar(matriculaId);
        if (matricula.getStatus() != StatusMatricula.VENCIDA && matricula.getStatus() != StatusMatricula.SUSPENSA) {
            throw new AcademiaException("Só é possível renovar matriculas VENCIDA ou SUSPENSA.");
        }
        Plano plano = dados.planos().buscarPorId(matricula.getPlanoId())
                .orElseThrow(() -> new AcademiaException("Plano da matricula nao encontrado."));

        LocalDate hoje = LocalDate.now();
        LocalDate novoVencimento = hoje.plusMonths(plano.getDuracaoMeses());

        Pagamento pagamento = new Pagamento(GeradorId.gerar("PAG"), matriculaId, plano.getValorMensal(), novoVencimento);
        pagamento.pagar(hoje); // renovacao com novo pagamento confirmado
        dados.pagamentos().adicionar(pagamento);

        matricula.renovar(novoVencimento); // VENCIDA/SUSPENSA -> ATIVA
    }

    public Matricula buscar(String matriculaId) {
        return dados.matriculas().buscarPorId(matriculaId)
                .orElseThrow(() -> new AcademiaException("Matricula nao encontrada: " + matriculaId));
    }

    public java.util.List<Matricula> listarPorAcademia(String academiaId) {
        return dados.matriculas().listarPorAcademia(academiaId);
    }
}
