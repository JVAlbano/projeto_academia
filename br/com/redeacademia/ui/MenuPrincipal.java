package br.com.redeacademia.ui;

import br.com.redeacademia.model.Academia;

import java.util.List;

/** Menu de nivel rede: gestao de academias e relatorio consolidado. */
public class MenuPrincipal {

    private final Console console;
    private final Servicos s;

    public MenuPrincipal(Console console, Servicos servicos) {
        this.console = console;
        this.s = servicos;
    }

    public void exibir() {
        boolean sair = false;
        while (!sair) {
            String nomeRede = s.dados.possuiRede() ? s.dados.getRede().getNome() : "(nenhuma rede)";
            console.titulo("REDE DE ACADEMIAS - " + nomeRede);
            if (!s.dados.possuiRede()) {
                console.msg("1) Criar rede");
                console.msg("0) Sair");
                try {
                    switch (console.lerInt("Opcao")) {
                        case 1 -> criarRede();
                        case 0 -> sair = true;
                        default -> console.erro("Opcao invalida.");
                    }
                } catch (RuntimeException e) {
                    console.erro(e.getMessage());
                    console.pausar();
                }
                continue;
            }

            console.msg("1) Cadastrar academia");
            console.msg("2) Listar academias");
            console.msg("3) Entrar em uma academia");
            console.msg("4) Atualizar academia");
            console.msg("5) Remover academia");
            console.msg("6) Relatorio consolidado da rede");
            console.msg("7) Salvar dados agora");
            console.msg("0) Sair");
            try {
                switch (console.lerInt("Opcao")) {
                    case 1 -> cadastrarAcademia();
                    case 2 -> listarAcademias();
                    case 3 -> entrarAcademia();
                    case 4 -> atualizarAcademia();
                    case 5 -> removerAcademia();
                    case 6 -> {
                        console.msg("\n" + s.relatorios.relatorioConsolidado());
                        console.pausar();
                    }
                    case 7 -> {
                        s.dados.salvar();
                        console.sucesso("Dados salvos em /data.");
                    }
                    case 0 -> sair = true;
                    default -> console.erro("Opcao invalida.");
                }
            } catch (br.com.redeacademia.exception.AcademiaException
                     | IllegalArgumentException | IllegalStateException e) {
                console.erro(e.getMessage());
                console.pausar();
            }
        }
    }

    private void criarRede() {
        s.rede.criarRede(
                console.lerTextoObrigatorio("Nome da rede"),
                console.lerTexto("CNPJ"),
                console.lerTexto("Site"));
        console.sucesso("Rede criada.");
    }

    private void cadastrarAcademia() {
        Academia a = s.rede.cadastrarAcademia(
                console.lerTextoObrigatorio("Nome"),
                console.lerTexto("Endereco"),
                console.lerTexto("Telefone"),
                console.lerInt("Capacidade maxima"));
        console.sucesso("Academia cadastrada: " + a.getId());
    }

    private void listarAcademias() {
        List<Academia> academias = s.dados.academias().listar();
        if (academias.isEmpty()) {
            console.msg("Nenhuma academia cadastrada.");
            return;
        }
        for (Academia a : academias) {
            String gerente = a.getGerenteId() == null ? "sem gerente" : "gerente " + a.getGerenteId();
            console.msg(String.format("- [%s] %s | %s | cap. %d | %s",
                    a.getId(), a.getNome(), a.getEndereco(), a.getCapacidadeMaxima(), gerente));
        }
    }

    private void entrarAcademia() {
        listarAcademias();
        Academia a = s.rede.buscarAcademia(console.lerTextoObrigatorio("ID da academia"));
        new MenuAcademia(console, s, a).exibir();
    }

    private void atualizarAcademia() {
        listarAcademias();
        String id = console.lerTextoObrigatorio("ID da academia");
        Academia a = s.rede.buscarAcademia(id); // valores atuais para pre-carregar
        s.rede.atualizarAcademia(id,
                console.lerTextoOuManter("Nome", a.getNome()),
                console.lerTextoOuManter("Endereco", a.getEndereco()),
                console.lerTextoOuManter("Telefone", a.getTelefone()),
                console.lerIntOuManter("Capacidade maxima", a.getCapacidadeMaxima()));
        console.sucesso("Academia atualizada.");
    }

    private void removerAcademia() {
        listarAcademias();
        s.rede.removerAcademia(console.lerTextoObrigatorio("ID da academia a remover"));
        console.sucesso("Academia removida.");
    }
}
