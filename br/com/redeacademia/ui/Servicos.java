package br.com.redeacademia.ui;

import br.com.redeacademia.service.AcessoService;
import br.com.redeacademia.service.ClienteService;
import br.com.redeacademia.service.Dados;
import br.com.redeacademia.service.FuncionarioService;
import br.com.redeacademia.service.InicializacaoService;
import br.com.redeacademia.service.MatriculaService;
import br.com.redeacademia.service.PagamentoService;
import br.com.redeacademia.service.PlanoService;
import br.com.redeacademia.service.RedeService;
import br.com.redeacademia.service.RelatorioService;
import br.com.redeacademia.service.TreinoService;

/** Reune o estado (Dados) e todos os services, simplificando a injecao nos menus. */
public class Servicos {

    public final Dados dados;
    public final RedeService rede;
    public final ClienteService clientes;
    public final FuncionarioService funcionarios;
    public final PlanoService planos;
    public final MatriculaService matriculas;
    public final PagamentoService pagamentos;
    public final TreinoService treinos;
    public final AcessoService acessos;
    public final RelatorioService relatorios;
    public final InicializacaoService inicializacao;

    public Servicos(Dados dados) {
        this.dados = dados;
        this.rede = new RedeService(dados);
        this.clientes = new ClienteService(dados);
        this.funcionarios = new FuncionarioService(dados);
        this.planos = new PlanoService(dados);
        this.matriculas = new MatriculaService(dados);
        this.pagamentos = new PagamentoService(dados);
        this.treinos = new TreinoService(dados);
        this.acessos = new AcessoService(dados);
        this.relatorios = new RelatorioService(dados);
        this.inicializacao = new InicializacaoService(dados);
    }
}
