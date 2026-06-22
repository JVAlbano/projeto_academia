package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.exception.AcessoNegadoException;
import br.com.redeacademia.exception.ClienteInadimplenteException;
import br.com.redeacademia.model.AcessoRegistro;
import br.com.redeacademia.model.enums.TipoAcesso;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.model.pessoa.Recepcionista;
import br.com.redeacademia.util.GeradorId;

import java.util.List;

/**
 * Registro de acessos na catraca. Aplica RN06 (inadimplente nao registra acesso)
 * e RN08 (so acessa academia coberta por uma matricula ativa; planos REDE cobrem todas).
 */
public class AcessoService {

    private final Dados dados;

    public AcessoService(Dados dados) {
        this.dados = dados;
    }

    /**
     * A recepcionista registra a entrada/saida de um cliente na sua academia.
     *
     * @throws ClienteInadimplenteException se o cliente tiver pagamento em atraso (RN06).
     * @throws AcessoNegadoException        se o cliente nao tiver matricula ativa que cubra
     *                                      esta academia (RN08) - planos REDE cobrem qualquer unidade.
     */
    public AcessoRegistro registrarAcesso(Recepcionista recepcionista, String clienteId, TipoAcesso tipo) {
        Cliente cliente = dados.clientes().buscarPorId(clienteId)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado: " + clienteId));
        if (Consultas.clienteInadimplente(dados, clienteId)) {
            throw new ClienteInadimplenteException(cliente.getNome()); // RN06
        }
        String academiaId = recepcionista.getAcademiaId();
        if (!Consultas.clienteTemAcessoNa(dados, clienteId, academiaId)) {
            throw AcessoNegadoException.semCobertura(cliente.getNome()); // RN08
        }
        AcessoRegistro acesso = recepcionista.registrarAcesso(GeradorId.gerar("ACE"), clienteId, tipo);
        dados.acessos().adicionar(acesso);
        return acesso;
    }

    public List<AcessoRegistro> listarPorAcademia(String academiaId) {
        return dados.acessos().listarPorAcademia(academiaId);
    }
}
