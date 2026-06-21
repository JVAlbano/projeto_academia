package br.com.redeacademia.model.pessoa;

/**
 * Superclasse abstrata dos funcionarios. Define o contrato polimorfico que cada
 * cargo implementa de forma propria:
 * <ul>
 *   <li>{@link #calcularComissao()} - regra de remuneracao variavel por cargo;</li>
 *   <li>{@link #gerarRelatorio()} - relatorio operacional especifico do cargo.</li>
 * </ul>
 *
 * <p>O modulo de folha de pagamento itera uma {@code List<Funcionario>} e chama
 * {@code calcularComissao()} sem conhecer o tipo concreto; o modulo de relatorios
 * faz o mesmo com {@code gerarRelatorio()} - dois pontos de extensao independentes.</p>
 */
public abstract class Funcionario extends Pessoa {

    private String matriculaFunc;
    private double salario;
    private String academiaId;

    protected Funcionario(String id, String nome, String cpf, String email, String telefone,
                          String matriculaFunc, double salario, String academiaId) {
        super(id, nome, cpf, email, telefone);
        this.matriculaFunc = matriculaFunc;
        setSalario(salario);
        this.academiaId = academiaId;
    }

    /** Remuneracao total a pagar (salario base + parcela variavel do cargo). */
    public abstract double calcularComissao();

    /** Relatorio operacional especifico do cargo. */
    public abstract String gerarRelatorio();

    /** Identificador textual do tipo, usado na serializacao polimorfica em JSON. */
    public abstract String getTipo();

    public String getMatriculaFunc() {
        return matriculaFunc;
    }

    public void setMatriculaFunc(String matriculaFunc) {
        this.matriculaFunc = matriculaFunc;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        if (salario < 0) {
            throw new IllegalArgumentException("Salario nao pode ser negativo.");
        }
        this.salario = salario;
    }

    public String getAcademiaId() {
        return academiaId;
    }

    public void setAcademiaId(String academiaId) {
        this.academiaId = academiaId;
    }
}
