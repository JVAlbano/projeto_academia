package br.com.redeacademia.model;

import br.com.redeacademia.exception.GerenteJaAtribuidoException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Unidade operacional da rede. Possui exatamente 1 gerente (RN03), alem de
 * funcionarios, clientes e planos proprios (vinculados pelo {@code academiaId}
 * de cada entidade).
 */
public class Academia {

    private final String id;
    private String nome;
    private String endereco;
    private String telefone;
    private int capacidadeMaxima;
    private String redeId;
    private String gerenteId; // id do gerente atual; null se ainda nao atribuido
    private boolean ativa;

    public Academia(String id, String nome, String endereco, String telefone,
                    int capacidadeMaxima, String redeId) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        setCapacidadeMaxima(capacidadeMaxima);
        this.redeId = redeId;
        this.ativa = true;
    }

    /**
     * Atribui um gerente a academia. (RN03: cada academia tem no maximo 1 gerente.)
     *
     * @throws GerenteJaAtribuidoException se ja houver um gerente atribuido.
     */
    public void atribuirGerente(String gerenteId) {
        if (this.gerenteId != null) {
            throw new GerenteJaAtribuidoException(nome);
        }
        this.gerenteId = gerenteId;
    }

    /** Remove o vinculo do gerente atual (ex.: ao demitir o gerente). */
    public void removerGerente() {
        this.gerenteId = null;
    }

    /** Filtra, da lista global, os planos pertencentes a esta academia. */
    public List<Plano> listarPlanos(List<Plano> todosOsPlanos) {
        return todosOsPlanos.stream()
                .filter(p -> id.equals(p.getAcademiaId()))
                .collect(Collectors.toList());
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade maxima deve ser maior que zero.");
        }
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public String getRedeId() {
        return redeId;
    }

    public void setRedeId(String redeId) {
        this.redeId = redeId;
    }

    public String getGerenteId() {
        return gerenteId;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    @Override
    public String toString() {
        return String.format("Academia{id=%s, nome=%s, endereco=%s, capacidade=%d, ativa=%b}",
                id, nome, endereco, capacidadeMaxima, ativa);
    }
}
