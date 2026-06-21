package br.com.redeacademia.model;

import br.com.redeacademia.exception.TransicaoEstadoInvalidaException;
import br.com.redeacademia.model.enums.StatusPagamento;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Pagamento vinculado a uma matricula. Possui ESTADO DINAMICO validado por
 * {@link #avancarEstado(StatusPagamento)}.
 *
 * <p>Transicoes permitidas:</p>
 * <pre>
 *   (criacao) -&gt; PENDENTE
 *   PENDENTE  -&gt; PAGO | ATRASADO (auto) | CANCELADO
 *   ATRASADO  -&gt; PAGO (com multa) | CANCELADO
 *   PAGO      -&gt; CANCELADO
 *   CANCELADO -&gt; (estado final)
 * </pre>
 */
public class Pagamento {

    /** Multa de 10% aplicada ao quitar um pagamento em atraso. */
    private static final double TAXA_MULTA = 0.10;

    private static final Map<StatusPagamento, Set<StatusPagamento>> TRANSICOES = new EnumMap<>(StatusPagamento.class);

    static {
        TRANSICOES.put(StatusPagamento.PENDENTE, EnumSet.of(StatusPagamento.PAGO, StatusPagamento.ATRASADO, StatusPagamento.CANCELADO));
        TRANSICOES.put(StatusPagamento.ATRASADO, EnumSet.of(StatusPagamento.PAGO, StatusPagamento.CANCELADO));
        TRANSICOES.put(StatusPagamento.PAGO, EnumSet.of(StatusPagamento.CANCELADO));
        TRANSICOES.put(StatusPagamento.CANCELADO, EnumSet.noneOf(StatusPagamento.class));
    }

    private final String id;
    private final String matriculaId;
    private double valor;
    private StatusPagamento status;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento; // null enquanto nao pago

    public Pagamento(String id, String matriculaId, double valor, LocalDate dataVencimento) {
        this.id = id;
        this.matriculaId = matriculaId;
        setValor(valor);
        this.dataVencimento = dataVencimento;
        this.status = StatusPagamento.PENDENTE;
    }

    /**
     * Valida e executa a transicao de estado.
     *
     * @throws TransicaoEstadoInvalidaException se a transicao nao for permitida.
     */
    public void avancarEstado(StatusPagamento novo) {
        Set<StatusPagamento> permitidos = TRANSICOES.getOrDefault(status, EnumSet.noneOf(StatusPagamento.class));
        if (!permitidos.contains(novo)) {
            throw new TransicaoEstadoInvalidaException("Pagamento", status, novo);
        }
        this.status = novo;
    }

    /** Quita o pagamento. Se estava ATRASADO, aplica multa antes de marcar como PAGO. */
    public void pagar(LocalDate dataPagamento) {
        if (status == StatusPagamento.ATRASADO) {
            this.valor = valor * (1 + TAXA_MULTA);
        }
        this.dataPagamento = dataPagamento;
        avancarEstado(StatusPagamento.PAGO);
    }

    /** Cancela o pagamento (estado final). */
    public void cancelar() {
        avancarEstado(StatusPagamento.CANCELADO);
    }

    /**
     * Verifica e, se for o caso, marca o pagamento como ATRASADO.
     *
     * @return true se o pagamento esta (ou passou a estar) em atraso.
     */
    public boolean verificarAtraso(LocalDate referencia) {
        if (status == StatusPagamento.PENDENTE && dataVencimento != null && dataVencimento.isBefore(referencia)) {
            avancarEstado(StatusPagamento.ATRASADO);
        }
        return status == StatusPagamento.ATRASADO;
    }

    public String getId() {
        return id;
    }

    public String getMatriculaId() {
        return matriculaId;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        if (valor < 0) {
            throw new IllegalArgumentException("Valor do pagamento nao pode ser negativo.");
        }
        this.valor = valor;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    /** Define o status diretamente (apenas para carga da persistencia). */
    public void restaurarStatus(StatusPagamento status) {
        this.status = status;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}
