package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.exception.ClienteInadimplenteException;
import br.com.redeacademia.model.Exercicio;
import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.NivelTreino;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.model.pessoa.Instrutor;
import br.com.redeacademia.util.GeradorId;

import java.util.List;

/** Gestao de treinos. Aplica RN06 (cliente inadimplente nao recebe novo treino). */
public class TreinoService {

    private final Dados dados;

    public TreinoService(Dados dados) {
        this.dados = dados;
    }

    /**
     * Instrutor monta um treino para um cliente.
     *
     * @throws ClienteInadimplenteException se o cliente tiver pagamento em atraso (RN06).
     */
    public Treino criarTreino(Instrutor instrutor, String clienteId, String nome,
                              String objetivo, NivelTreino nivel, int duracaoMinutos) {
        Cliente cliente = dados.clientes().buscarPorId(clienteId)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado: " + clienteId));
        if (Consultas.clienteInadimplente(dados, clienteId)) {
            throw new ClienteInadimplenteException(cliente.getNome()); // RN06
        }
        Treino treino = instrutor.montarTreino(GeradorId.gerar("TR"), nome, objetivo, nivel, duracaoMinutos, clienteId);
        dados.treinos().adicionar(treino);
        return treino;
    }

    public void adicionarExercicio(String treinoId, String nome, String grupoMuscular,
                                   int series, int repeticoes, double carga) {
        Treino treino = buscar(treinoId);
        treino.adicionarExercicio(new Exercicio(nome, grupoMuscular, series, repeticoes, carga));
    }

    /** Atualiza os dados basicos de um treino. */
    public void atualizar(String treinoId, String nome, String objetivo,
                          NivelTreino nivel, int duracaoMinutos) {
        Treino treino = buscar(treinoId);
        treino.setNome(nome);
        treino.setObjetivo(objetivo);
        treino.setNivel(nivel);
        treino.setDuracaoMinutos(duracaoMinutos);
    }

    /** Remove um exercicio do treino (pelo nome). */
    public void removerExercicio(String treinoId, String nomeExercicio) {
        Treino treino = buscar(treinoId);
        if (!treino.removerExercicio(nomeExercicio)) {
            throw new AcademiaException("Exercicio nao encontrado no treino: " + nomeExercicio);
        }
    }

    public List<Treino> listarPorCliente(String clienteId) {
        return dados.treinos().listarPorCliente(clienteId);
    }

    public void remover(String treinoId) {
        dados.treinos().remover(treinoId);
    }

    public Treino buscar(String treinoId) {
        return dados.treinos().buscarPorId(treinoId)
                .orElseThrow(() -> new AcademiaException("Treino nao encontrado: " + treinoId));
    }
}
