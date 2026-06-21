package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaNaoEncontradaException;
import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.RedeAcademica;
import br.com.redeacademia.util.GeradorId;

/** Operacoes da rede: criacao da rede e CRUD de academias. */
public class RedeService {

    private final Dados dados;

    public RedeService(Dados dados) {
        this.dados = dados;
    }

    /** Cria a rede (singleton de negocio). Substitui eventual rede existente. */
    public RedeAcademica criarRede(String nome, String cnpj, String site) {
        RedeAcademica rede = new RedeAcademica(GeradorId.gerar("REDE"), nome, cnpj, site);
        dados.setRede(rede);
        return rede;
    }

    public Academia cadastrarAcademia(String nome, String endereco, String telefone, int capacidade) {
        exigirRede();
        Academia academia = new Academia(GeradorId.gerar("ACAD"), nome, endereco, telefone,
                capacidade, dados.getRede().getId());
        dados.academias().adicionar(academia);
        dados.getRede().adicionarAcademia(academia);
        return academia;
    }

    public Academia buscarAcademia(String id) {
        return dados.academias().buscarPorId(id)
                .orElseThrow(() -> new AcademiaNaoEncontradaException(id));
    }

    public void atualizarAcademia(String id, String nome, String endereco, String telefone, int capacidade) {
        Academia a = buscarAcademia(id);
        a.setNome(nome);
        a.setEndereco(endereco);
        a.setTelefone(telefone);
        a.setCapacidadeMaxima(capacidade);
    }

    public void removerAcademia(String id) {
        buscarAcademia(id); // valida existencia (lanca se nao encontrar)
        dados.academias().remover(id);
        dados.getRede().removerAcademia(id);
    }

    private void exigirRede() {
        if (!dados.possuiRede()) {
            throw new IllegalStateException("Nenhuma rede cadastrada. Crie a rede primeiro.");
        }
    }
}
