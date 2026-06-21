package br.com.redeacademia.model;

import br.com.redeacademia.exception.TransicaoEstadoInvalidaException;
import br.com.redeacademia.model.enums.StatusMatricula;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Vinculo de um cliente a um plano de uma academia. Possui ESTADO DINAMICO:
 * a transicao entre estados e validada por {@link #avancarEstado(StatusMatricula)}.
 *
 * <p>Transicoes permitidas:</p>
 * <pre>
 *   (criacao) -&gt; SUSPENSA (aguardando 1o pagamento)
 *   SUSPENSA  -&gt; ATIVA (pagamento confirmado) | CANCELADA
 *   ATIVA     -&gt; SUSPENSA (inadimplencia) | VENCIDA (auto) | CANCELADA
 *   VENCIDA   -&gt; ATIVA (renovacao) | CANCELADA
 *   CANCELADA -&gt; (estado final)
 * </pre>
 */
public class Matricula {

    private static final Map<StatusMatricula, Set<StatusMatricula>> TRANSICOES = new EnumMap<>(StatusMatricula.class);

    static {
        TRANSICOES.put(StatusMatricula.SUSPENSA, EnumSet.of(StatusMatricula.ATIVA, StatusMatricula.CANCELADA));
        TRANSICOES.put(StatusMatricula.ATIVA, EnumSet.of(StatusMatricula.SUSPENSA, StatusMatricula.VENCIDA, StatusMatricula.CANCELADA));
        TRANSICOES.put(StatusMatricula.VENCIDA, EnumSet.of(StatusMatricula.ATIVA, StatusMatricula.CANCELADA));
        TRANSICOES.put(StatusMatricula.CANCELADA, EnumSet.noneOf(StatusMatricula.class));
    }

    private final String id;
    private final String clienteId;
    private final String planoId;
    private final String academiaId;
    private StatusMatricula status;
    private LocalDate dataInicio;
    private LocalDate dataVencimento;

    public Matricula(String id, String clienteId, String planoId, String academiaId,
                     LocalDate dataInicio, LocalDate dataVencimento) {
        this.id = id;
        this.clienteId = clienteId;
        this.planoId = planoId;
        this.academiaId = academiaId;
        this.dataInicio = dataInicio;
        this.dataVencimento = dataVencimento;
        this.status = StatusMatricula.SUSPENSA; // nasce inativa, aguardando pagamento (RN05)
    }

    /**
     * Valida e executa a transicao de estado.
     *
     * @throws TransicaoEstadoInvalidaException se a transicao nao for permitida.
     */
    public void avancarEstado(StatusMatricula novo) {
        Set<StatusMatricula> permitidos = TRANSICOES.getOrDefault(status, EnumSet.noneOf(StatusMatricula.class));
        if (!permitidos.contains(novo)) {
            throw new TransicaoEstadoInvalidaException("Matricula", status, novo);
        }
        this.status = novo;
    }

    /** Ativa a matricula (SUSPENSA/VENCIDA -> ATIVA). A RN05 e verificada no service. */
    public void ativar() {
        avancarEstado(StatusMatricula.ATIVA);
    }

    /** Suspende a matricula por inadimplencia (ATIVA -> SUSPENSA). */
    public void suspender() {
        avancarEstado(StatusMatricula.SUSPENSA);
    }

    /** Cancela a matricula (estado final). */
    public void cancelar() {
        avancarEstado(StatusMatricula.CANCELADA);
    }

    /** Marca como vencida automaticamente (ATIVA -> VENCIDA). Usado pela RN07. */
    public void marcarVencida() {
        avancarEstado(StatusMatricula.VENCIDA);
    }

    /** Renova a matricula com novo vencimento (VENCIDA/SUSPENSA -> ATIVA). */
    public void renovar(LocalDate novoVencimento) {
        this.dataVencimento = novoVencimento;
        avancarEstado(StatusMatricula.ATIVA);
    }

    public boolean estaVencida(LocalDate referencia) {
        return dataVencimento != null && dataVencimento.isBefore(referencia);
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getPlanoId() {
        return planoId;
    }

    public String getAcademiaId() {
        return academiaId;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    /** Define o status diretamente (apenas para carga da persistencia). */
    public void restaurarStatus(StatusMatricula status) {
        this.status = status;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }
}
