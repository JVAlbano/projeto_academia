package br.com.redeacademia.service;

import br.com.redeacademia.exception.AcademiaException;
import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.pessoa.Funcionario;
import br.com.redeacademia.model.pessoa.Gerente;

import java.util.List;

/**
 * Gestao de funcionarios: contratacao/demissao com autorizacao (RN04), atribuicao
 * de gerente (RN03), unicidade de CPF (RN01) e os dois fluxos polimorficos
 * (folha de pagamento e relatorios operacionais).
 */
public class FuncionarioService {

    private final Dados dados;
    private final ContextoFuncionario contexto;

    public FuncionarioService(Dados dados) {
        this.dados = dados;
        this.contexto = new ContextoFuncionario(dados);
    }

    /**
     * Atribui um gerente a academia (operacao de nivel rede). Aplica RN01 e RN03.
     *
     * @throws br.com.redeacademia.exception.GerenteJaAtribuidoException se ja houver gerente.
     */
    public void contratarGerente(Academia academia, Gerente novoGerente) {
        RegrasCpf.garantirCpfUnico(dados, novoGerente.getCpf());
        academia.atribuirGerente(novoGerente.getId()); // RN03
        dados.funcionarios().adicionar(novoGerente);
    }

    /**
     * Contrata um funcionario operacional (instrutor/recepcionista). Aplica RN04
     * (autorizacao do gerente) e RN01.
     */
    public void contratarFuncionario(Gerente gerenteAtuante, Academia academia, Funcionario novo) {
        if (novo instanceof Gerente) {
            throw new AcademiaException("Use a atribuicao de gerente (nivel rede) para contratar um gerente.");
        }
        gerenteAtuante.contratarFuncionario(academia, novo); // RN04 (autorizacao) + vincula academiaId
        RegrasCpf.garantirCpfUnico(dados, novo.getCpf());
        dados.funcionarios().adicionar(novo);
    }

    /** Demite um funcionario. Aplica RN04 (autorizacao do gerente). */
    public void demitir(Gerente gerenteAtuante, Academia academia, Funcionario alvo) {
        gerenteAtuante.demitirFuncionario(academia, alvo); // RN04
        if (alvo.getId().equals(academia.getGerenteId())) {
            academia.removerGerente();
        }
        dados.funcionarios().remover(alvo.getId());
    }

    public Funcionario buscarPorId(String id) {
        return dados.funcionarios().buscarPorId(id)
                .orElseThrow(() -> new AcademiaException("Funcionario nao encontrado: " + id));
    }

    public Gerente buscarGerenteDaAcademia(Academia academia) {
        if (academia.getGerenteId() == null) {
            throw new AcademiaException("A academia '" + academia.getNome() + "' ainda nao possui gerente.");
        }
        Funcionario f = buscarPorId(academia.getGerenteId());
        if (!(f instanceof Gerente g)) {
            throw new AcademiaException("Inconsistencia: o gerente registrado nao e um Gerente.");
        }
        return g;
    }

    public List<Funcionario> listarPorAcademia(String academiaId) {
        return dados.funcionarios().listarPorAcademia(academiaId);
    }

    /** Folha de pagamento: itera os funcionarios e usa {@code calcularComissao()} polimorfico. */
    public String gerarFolhaPagamento(String academiaId) {
        StringBuilder sb = new StringBuilder("===== FOLHA DE PAGAMENTO =====\n");
        double total = 0;
        for (Funcionario f : listarPorAcademia(academiaId)) {
            contexto.preparar(f);
            double comissao = f.calcularComissao();
            total += comissao;
            sb.append(String.format("- %-20s [%-13s] R$ %,.2f%n", f.getNome(), f.getTipo(), comissao));
        }
        sb.append(String.format("TOTAL DA FOLHA: R$ %,.2f", total));
        return sb.toString();
    }

    /** Relatorios operacionais: itera os funcionarios e usa {@code gerarRelatorio()} polimorfico. */
    public String gerarRelatoriosOperacionais(String academiaId) {
        StringBuilder sb = new StringBuilder();
        List<Funcionario> funcionarios = listarPorAcademia(academiaId);
        if (funcionarios.isEmpty()) {
            return "Nenhum funcionario cadastrado nesta academia.";
        }
        for (Funcionario f : funcionarios) {
            contexto.preparar(f);
            sb.append(f.gerarRelatorio()).append("\n\n");
        }
        return sb.toString().strip();
    }
}
