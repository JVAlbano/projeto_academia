package model;

public abstract class Pessoa {
    protected String id;
    protected String nome;
    protected String cpf;
    protected String telefone;
    protected String email;

    public Pessoa(String id, String nome, String cpf, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return String.format("%s [id=%s, nome=%s, cpf=%s, telefone=%s, email=%s]",
                getClass().getSimpleName(), id, nome, cpf, telefone, email);
    }
}
