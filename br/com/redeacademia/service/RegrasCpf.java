package br.com.redeacademia.service;

import br.com.redeacademia.exception.CpfDuplicadoException;
import br.com.redeacademia.util.ValidadorCpf;

/** RN01 (unicidade): garante que nenhum CPF se repita entre clientes e funcionarios. */
public final class RegrasCpf {

    private RegrasCpf() {
    }

    public static void garantirCpfUnico(Dados dados, String cpf) {
        String alvo = ValidadorCpf.normalizar(cpf);
        boolean existeCliente = dados.clientes().listar().stream()
                .anyMatch(c -> alvo.equals(ValidadorCpf.normalizar(c.getCpf())));
        boolean existeFuncionario = dados.funcionarios().listar().stream()
                .anyMatch(f -> alvo.equals(ValidadorCpf.normalizar(f.getCpf())));
        if (existeCliente || existeFuncionario) {
            throw new CpfDuplicadoException(ValidadorCpf.formatar(cpf));
        }
    }
}
