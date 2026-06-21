package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.exception.ClienteInadimplenteException;
import br.com.redeacademia.model.AcessoRegistro;
import br.com.redeacademia.model.enums.TipoAcesso;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.model.pessoa.Recepcionista;
import br.com.redeacademia.util.GeradorId;

import java.util.List;

/** Registro de acessos na catraca. Aplica RN06 (inadimplente nao registra acesso). */
public class AcessoService {

    private final Dados dados;

    public AcessoService(Dados dados) {
        this.dados = dados;
    }

    /**
     * A recepcionista registra a entrada/saida de um cliente.
     *
     * @throws ClienteInadimplenteException se o cliente tiver pagamento em atraso (RN06).
     */
    public AcessoRegistro registrarAcesso(Recepcionista recepcionista, String clienteId, TipoAcesso tipo) {
        Cliente cliente = dados.clientes().buscarPorId(clienteId)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado: " + clienteId));
        if (Consultas.clienteInadimplente(dados, clienteId)) {
            throw new ClienteInadimplenteException(cliente.getNome()); // RN06
        }
        AcessoRegistro acesso = recepcionista.registrarAcesso(GeradorId.gerar("ACE"), clienteId, tipo);
        dados.acessos().adicionar(acesso);
        return acesso;
    }

    public List<AcessoRegistro> listarPorAcademia(String academiaId) {
        return dados.acessos().listarPorAcademia(academiaId);
    }
}
