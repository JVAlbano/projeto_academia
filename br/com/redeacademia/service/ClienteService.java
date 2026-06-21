package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.model.enums.StatusMatricula;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.util.GeradorId;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** CRUD de clientes, aplicando a RN01 (CPF unico e valido). */
public class ClienteService {

    private final Dados dados;

    public ClienteService(Dados dados) {
        this.dados = dados;
    }

    public Cliente cadastrar(String academiaId, String nome, String cpf, String email, String telefone,
                             LocalDate dataNascimento, String objetivo, double peso, double altura) {
        RegrasCpf.garantirCpfUnico(dados, cpf); // RN01 (unicidade)
        // RN01 (formato/digitos) e validada no construtor de Pessoa.
        Cliente cliente = new Cliente(GeradorId.gerar("CLI"), nome, cpf, email, telefone,
                dataNascimento, objetivo, peso, altura, academiaId);
        dados.clientes().adicionar(cliente);
        return cliente;
    }

    public Cliente buscarPorId(String id) {
        return dados.clientes().buscarPorId(id)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado: " + id));
    }

    public Cliente buscarPorCpf(String cpf) {
        return dados.clientes().buscarPorCpf(cpf)
                .orElseThrow(() -> new AcademiaException("Cliente nao encontrado para o CPF: " + cpf));
    }

    public void atualizar(String id, String nome, String email, String telefone,
                          String objetivo, double peso, double altura) {
        Cliente c = buscarPorId(id);
        c.setNome(nome);
        c.setEmail(email);
        c.setTelefone(telefone);
        c.setObjetivo(objetivo);
        c.setPeso(peso);
        c.setAltura(altura);
    }

    /** Remove o cliente. Bloqueia se houver matricula ATIVA vinculada. */
    public void remover(String id) {
        boolean possuiMatriculaAtiva = dados.matriculas().listarPorCliente(id).stream()
                .anyMatch(m -> m.getStatus() == StatusMatricula.ATIVA);
        if (possuiMatriculaAtiva) {
            throw new AcademiaException("Nao e possivel remover: o cliente possui matricula ativa.");
        }
        dados.clientes().remover(id);
    }

    public List<Cliente> listarPorAcademia(String academiaId) {
        return dados.clientes().listar().stream()
                .filter(c -> academiaId.equals(c.getAcademiaId()))
                .collect(Collectors.toList());
    }
}
