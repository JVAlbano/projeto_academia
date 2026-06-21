package br.com.redeacademia.model;

import br.com.redeacademia.model.enums.TipoAcesso;

import java.time.LocalDateTime;

/**
 * Registro de um acesso (entrada/saida) de um cliente em uma academia.
 * Gerado pela recepcionista e particionado por academia e data na persistencia.
 */
public class AcessoRegistro {

    private final String id;
    private final String clienteId;
    private final String academiaId;
    private final LocalDateTime dataHora;
    private final TipoAcesso tipo;

    public AcessoRegistro(String id, String clienteId, String academiaId,
                          LocalDateTime dataHora, TipoAcesso tipo) {
        this.id = id;
        this.clienteId = clienteId;
        this.academiaId = academiaId;
        this.dataHora = dataHora;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getClienteId() {
        return clienteId;
    }

    public String getAcademiaId() {
        return academiaId;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public TipoAcesso getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return String.format("Acesso{cliente=%s, tipo=%s, em=%s}", clienteId, tipo, dataHora);
    }
}
