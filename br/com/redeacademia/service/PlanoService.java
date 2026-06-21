package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.model.pessoa.Gerente;
import br.com.redeacademia.util.GeradorId;

import java.util.List;

/** CRUD de planos por academia. Operacoes administrativas exigem o gerente (RN04). */
public class PlanoService {

    private final Dados dados;

    public PlanoService(Dados dados) {
        this.dados = dados;
    }

    public Plano criar(Gerente gerenteAtuante, Academia academia, String nome, double valorMensal, int duracaoMeses) {
        gerenteAtuante.autorizarOperacaoNa(academia, "gerenciar planos"); // RN04
        Plano plano = new Plano(GeradorId.gerar("PLANO"), nome, valorMensal, duracaoMeses, academia.getId());
        dados.planos().adicionar(plano);
        return plano;
    }

    public void atualizar(String planoId, String nome, double valorMensal, int duracaoMeses) {
        Plano p = buscarPorId(planoId);
        p.setNome(nome);
        p.setValorMensal(valorMensal);
        p.setDuracaoMeses(duracaoMeses);
    }

    /** Remove o plano. Bloqueia se houver matricula ATIVA associada. */
    public void remover(Gerente gerenteAtuante, Academia academia, String planoId) {
        gerenteAtuante.autorizarOperacaoNa(academia, "gerenciar planos"); // RN04
        boolean possuiMatriculaAtiva = dados.matriculas().listar().stream()
                .anyMatch(m -> planoId.equals(m.getPlanoId()) && m.getStatus() == StatusMatricula.ATIVA);
        if (possuiMatriculaAtiva) {
            throw new AcademiaException("Nao e possivel remover: ha matricula ativa neste plano.");
        }
        dados.planos().remover(planoId);
    }

    public Plano buscarPorId(String planoId) {
        return dados.planos().buscarPorId(planoId)
                .orElseThrow(() -> new AcademiaException("Plano nao encontrado: " + planoId));
    }

    public List<Plano> listarPorAcademia(String academiaId) {
        return dados.planos().listarPorAcademia(academiaId);
    }
}
