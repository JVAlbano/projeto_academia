package br.com.redeacademia.service;

import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Pagamento;
import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.NivelAcesso;
import br.com.redeacademia.model.enums.NivelTreino;
import br.com.redeacademia.model.enums.TurnoFuncionario;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.model.pessoa.Gerente;
import br.com.redeacademia.model.pessoa.Instrutor;
import br.com.redeacademia.model.pessoa.Recepcionista;
import br.com.redeacademia.util.GeradorId;
import br.com.redeacademia.util.ValidadorCpf;

import java.time.LocalDate;

/**
 * Popula o sistema com dados de exemplo na primeira execucao (quando nao ha rede
 * persistida), para facilitar a demonstracao. Usa os services, de modo que todas
 * as regras de negocio sao exercidas tambem na geracao do seed.
 */
public class SeedDados {

    private final Dados dados;

    public SeedDados(Dados dados) {
        this.dados = dados;
    }

    public void popular() {
        RedeService rede = new RedeService(dados);
        FuncionarioService func = new FuncionarioService(dados);
        ClienteService cli = new ClienteService(dados);
        PlanoService planoSvc = new PlanoService(dados);
        MatriculaService matSvc = new MatriculaService(dados);
        PagamentoService pagSvc = new PagamentoService(dados);
        TreinoService treSvc = new TreinoService(dados);

        rede.criarRede("RedeFit Brasil", "12.345.678/0001-90", "www.redefit.com.br");
        Academia centro = rede.cadastrarAcademia("Unidade Centro", "Rua Principal, 100", "(11) 1111-1111", 200);
        Academia norte = rede.cadastrarAcademia("Unidade Norte", "Av. Norte, 500", "(11) 2222-2222", 150);

        // ---- Unidade Centro: equipe ----
        Gerente ana = new Gerente(GeradorId.gerar("FUN"), "Ana Gerente", ValidadorCpf.gerarValido("529982247"),
                "ana@redefit.com", "(11) 90000-0001", "G-001", 6000, centro.getId(), NivelAcesso.MASTER, 0.25);
        func.contratarGerente(centro, ana);

        Instrutor bob = new Instrutor(GeradorId.gerar("FUN"), "Bob Instrutor", ValidadorCpf.gerarValido("168995350"),
                "bob@redefit.com", "(11) 90000-0002", "I-001", 2800, centro.getId(), "CREF-12345", "Musculacao", 40);
        func.contratarFuncionario(ana, centro, bob);

        Recepcionista cleo = new Recepcionista(GeradorId.gerar("FUN"), "Cleo Recepcao", ValidadorCpf.gerarValido("111444777"),
                "cleo@redefit.com", "(11) 90000-0003", "R-001", 1800, centro.getId(), TurnoFuncionario.NOTURNO, "senha123");
        func.contratarFuncionario(ana, centro, cleo);

        // ---- Unidade Centro: planos ----
        Plano anual = planoSvc.criar(ana, centro, "Plano Anual", 100, 12);
        Plano mensal = planoSvc.criar(ana, centro, "Plano Mensal", 130, 1);

        // ---- Unidade Centro: clientes ----
        Cliente joao = cli.cadastrar(centro.getId(), "Joao Silva", ValidadorCpf.gerarValido("123456789"),
                "joao@email.com", "(11) 98888-0001", LocalDate.of(1995, 7, 20), "Hipertrofia", 82, 1.78);
        Cliente maria = cli.cadastrar(centro.getId(), "Maria Souza", ValidadorCpf.gerarValido("987654321"),
                "maria@email.com", "(11) 98888-0002", LocalDate.of(2000, 3, 10), "Emagrecimento", 65, 1.65);

        // ---- Matriculas e pagamentos ----
        // Joao: matricula no anual, paga e ativa.
        Matricula mJoao = matSvc.criarMatricula(joao.getId(), anual.getId());
        Pagamento pJoao = pagSvc.listarPorMatricula(mJoao.getId()).get(0);
        pagSvc.registrarPagamento(pJoao.getId());
        matSvc.ativarMatricula(mJoao.getId());

        // Maria: matricula no mensal, pagamento deixado PENDENTE (aguardando).
        matSvc.criarMatricula(maria.getId(), mensal.getId());

        // ---- Treino do Joao montado pelo Bob ----
        Treino treino = treSvc.criarTreino(bob, joao.getId(), "Treino A - Superiores",
                "Hipertrofia", NivelTreino.INTERMEDIARIO, 60);
        treSvc.adicionarExercicio(treino.getId(), "Supino reto", "Peito", 4, 10, 40);
        treSvc.adicionarExercicio(treino.getId(), "Remada curvada", "Costas", 4, 12, 35);

        // ---- Unidade Norte: gerente, plano e cliente ----
        Gerente davi = new Gerente(GeradorId.gerar("FUN"), "Davi Gerente", ValidadorCpf.gerarValido("111222333"),
                "davi@redefit.com", "(11) 90000-0010", "G-002", 5800, norte.getId(), NivelAcesso.ADMINISTRATIVO, 0.20);
        func.contratarGerente(norte, davi);
        planoSvc.criar(davi, norte, "Plano Trimestral", 115, 3);
        cli.cadastrar(norte.getId(), "Lia Costa", ValidadorCpf.gerarValido("222333444"),
                "lia@email.com", "(11) 98888-0010", LocalDate.of(1998, 11, 5), "Condicionamento", 70, 1.70);
    }
}
