import br.com.redeacademia.service.Dados;
import br.com.redeacademia.ui.Console;
import br.com.redeacademia.ui.MenuPrincipal;
import br.com.redeacademia.ui.Servicos;


public class Main {

    public static void main(String[] args) {
        Dados dados = new Dados();
        dados.carregar();
        Servicos servicos = new Servicos(dados);

        System.out.println("[Sistema] " + servicos.inicializacao.aplicarRegrasTemporais());

        // Persiste o estado ao encerrar (inclusive com Ctrl+C).
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            dados.salvar();
            System.out.println("\n[Sistema] Dados salvos em /data. Ate logo!");
        }));

        new MenuPrincipal(new Console(), servicos).exibir();
    }
}
