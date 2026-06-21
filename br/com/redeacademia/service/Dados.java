package br.com.redeacademia.service;

import br.com.redeacademia.model.RedeAcademica;
import br.com.redeacademia.repository.AcademiaRepository;
import br.com.redeacademia.repository.AcessoRepository;
import br.com.redeacademia.repository.ClienteRepository;
import br.com.redeacademia.repository.FuncionarioRepository;
import br.com.redeacademia.repository.MatriculaRepository;
import br.com.redeacademia.repository.PagamentoRepository;
import br.com.redeacademia.repository.PlanoRepository;
import br.com.redeacademia.repository.RedeRepository;
import br.com.redeacademia.repository.TreinoRepository;

/**
 * Agregador central dos repositorios e da rede em memoria. Coordena a carga
 * inicial (a partir dos arquivos JSON) e a gravacao de todo o estado.
 */
public class Dados {

    private final RedeRepository redeRepo = new RedeRepository();
    private final AcademiaRepository academiaRepo = new AcademiaRepository();
    private final ClienteRepository clienteRepo = new ClienteRepository();
    private final FuncionarioRepository funcionarioRepo = new FuncionarioRepository();
    private final PlanoRepository planoRepo = new PlanoRepository();
    private final MatriculaRepository matriculaRepo = new MatriculaRepository();
    private final PagamentoRepository pagamentoRepo = new PagamentoRepository();
    private final TreinoRepository treinoRepo = new TreinoRepository();
    private final AcessoRepository acessoRepo = new AcessoRepository();

    private RedeAcademica rede;

    /** Le todos os arquivos JSON e reconstroi o estado em memoria. */
    public void carregar() {
        academiaRepo.carregar();
        clienteRepo.carregar();
        funcionarioRepo.carregar();
        planoRepo.carregar();
        matriculaRepo.carregar();
        pagamentoRepo.carregar();
        treinoRepo.carregar();
        acessoRepo.carregar();

        rede = redeRepo.carregar().orElse(null);
        if (rede != null) {
            academiaRepo.listar().forEach(rede::adicionarAcademia);
        }
    }

    /** Persiste todo o estado atual nos arquivos JSON. */
    public void salvar() {
        if (rede != null) {
            redeRepo.salvar(rede);
        }
        academiaRepo.salvar();
        clienteRepo.salvar();
        funcionarioRepo.salvar();
        planoRepo.salvar();
        matriculaRepo.salvar();
        pagamentoRepo.salvar();
        treinoRepo.salvar();
        acessoRepo.salvar();
    }

    public boolean possuiRede() {
        return rede != null;
    }

    public RedeAcademica getRede() {
        return rede;
    }

    public void setRede(RedeAcademica rede) {
        this.rede = rede;
    }

    public AcademiaRepository academias() {
        return academiaRepo;
    }

    public ClienteRepository clientes() {
        return clienteRepo;
    }

    public FuncionarioRepository funcionarios() {
        return funcionarioRepo;
    }

    public PlanoRepository planos() {
        return planoRepo;
    }

    public MatriculaRepository matriculas() {
        return matriculaRepo;
    }

    public PagamentoRepository pagamentos() {
        return pagamentoRepo;
    }

    public TreinoRepository treinos() {
        return treinoRepo;
    }

    public AcessoRepository acessos() {
        return acessoRepo;
    }
}
