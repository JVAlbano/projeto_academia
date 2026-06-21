import br.com.redeacademia.service.Dados;
import br.com.redeacademia.service.SeedDados;
import br.com.redeacademia.ui.Console;
import br.com.redeacademia.ui.MenuPrincipal;
import br.com.redeacademia.ui.Servicos;

/**
 * Ponto de entrada do Sistema de Gerenciamento de Rede de Academias.
 *
 * <p>Fluxo de inicializacao:</p>
 * <ol>
 *   <li>carrega o estado dos arquivos JSON em /data;</li>
 *   <li>na primeira execucao (sem dados), gera dados de exemplo;</li>
 *   <li>aplica as regras temporais automaticas (RN07);</li>
 *   <li>registra um shutdown hook que persiste tudo ao encerrar;</li>
 *   <li>inicia o menu interativo de terminal.</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        Dados dados = new Dados();
        dados.carregar();
        Servicos servicos = new Servicos(dados);

        if (!dados.possuiRede() && dados.academias().quantidade() == 0) {
            new SeedDados(dados).popular();
            System.out.println("[Sistema] Primeira execucao: dados de exemplo gerados.");
        }

        System.out.println("[Sistema] " + servicos.inicializacao.aplicarRegrasTemporais());

        // Persiste o estado ao encerrar (inclusive com Ctrl+C).
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dados.salvar();
            System.out.println("\n[Sistema] Dados salvos em /data. Ate logo!");
        }));

        new MenuPrincipal(new Console(), servicos).exibir();
    }
}
