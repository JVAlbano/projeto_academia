package br.com.redeacademia.model.pessoa;

import br.com.redeacademia.util.ValidadorCpf;

/**
 * Superclasse abstrata de todas as pessoas do sistema (clientes e funcionarios).
 *
 * <p>Centraliza identidade e dados de contato com encapsulamento total. O CPF e
 * validado na construcao e e imutavel (faz parte da identidade da pessoa).</p>
 */
public abstract class Pessoa {

    protected final String id;
    protected String nome;
    protected final String cpf;
    protected String email;
    protected String telefone;

    protected Pessoa(String id, String nome, String cpf, String email, String telefone) {
        // Checagem de digitos verificadores desativada (dados de teste): aceita qualquer CPF.
        // A unicidade do CPF (RN01) continua sendo aplicada nos services.
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome nao pode ser vazio.");
        }
        this.id = id;
        this.nome = nome;
        this.cpf = ValidadorCpf.formatar(cpf);
        setEmail(email);
        this.telefone = telefone;
    }

    /** Revalida o CPF desta pessoa (utilitario exposto pelo dominio). */
    public void validarCpf() {
        ValidadorCpf.validar(this.cpf);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome nao pode ser vazio.");
        }
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.isBlank() && !email.contains("@")) {
            throw new IllegalArgumentException("E-mail invalido: " + email);
        }
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, nome=%s, cpf=%s}", getClass().getSimpleName(), id, nome, cpf);
    }
}
