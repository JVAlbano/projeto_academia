package br.com.redeacademia.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Raiz administrativa da hierarquia (singleton de negocio: uma rede por sistema).
 * Mantem em memoria o conjunto de academias e consolida dados para relatorios
 * gerenciais centralizados.
 */
public class RedeAcademica {

    private final String id;
    private String nome;
    private String cnpj;
    private String site;
    private final List<Academia> academias = new ArrayList<>();

    public RedeAcademica(String id, String nome, String cnpj, String site) {
        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.site = site;
    }

    /** Cadastra uma academia na rede (ignora duplicatas por id). */
    public void adicionarAcademia(Academia academia) {
        boolean jaExiste = academias.stream().anyMatch(a -> a.getId().equals(academia.getId()));
        if (!jaExiste) {
            academias.add(academia);
        }
    }

    /** Remove a academia de id informado. @return true se removeu. */
    public boolean removerAcademia(String academiaId) {
        return academias.removeIf(a -> a.getId().equals(academiaId));
    }

    /** Lista (copia defensiva) das academias da rede. */
    public List<Academia> listarAcademias() {
        return new ArrayList<>(academias);
    }

    public Academia buscarAcademia(String academiaId) {
        return academias.stream()
                .filter(a -> a.getId().equals(academiaId))
                .findFirst()
                .orElse(null);
    }

    public int quantidadeAcademias() {
        return academias.size();
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
