package br.com.redeacademia.ui;

import br.com.redeacademia.model.Academia;
import br.com.redeacademia.model.Matricula;
import br.com.redeacademia.model.Pagamento;
import br.com.redeacademia.model.Plano;
import br.com.redeacademia.model.Treino;
import br.com.redeacademia.model.enums.NivelAcesso;
import br.com.redeacademia.model.enums.NivelTreino;
import br.com.redeacademia.model.enums.TipoAcesso;
import br.com.redeacademia.model.enums.TurnoFuncionario;
import br.com.redeacademia.model.pessoa.Cliente;
import br.com.redeacademia.model.pessoa.Funcionario;
import br.com.redeacademia.model.pessoa.Gerente;
import br.com.redeacademia.model.pessoa.Instrutor;
import br.com.redeacademia.model.pessoa.Recepcionista;

import java.util.List;

/** Menu de operacoes no contexto de uma academia selecionada. */
public class MenuAcademia {

    private final Console console;
    private final Servicos s;
    private final Academia academia;

    public MenuAcademia(Console console, Servicos servicos, Academia academia) {
        this.console = console;
        this.s = servicos;
        this.academia = academia;
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            console.titulo("Academia: " + academia.getNome());
            console.msg("1) Funcionarios");
            console.msg("2) Clientes");
            console.msg("3) Planos");
            console.msg("4) Matriculas e Pagamentos");
            console.msg("5) Treinos");
            console.msg("6) Acessos (catraca)");
            console.msg("0) Voltar");
            try {
                switch (console.lerInt("Opcao")) {
                    case 1 -> menuFuncionarios();
                    case 2 -> menuClientes();
                    case 3 -> menuPlanos();
                    case 4 -> menuMatriculas();
                    case 5 -> menuTreinos();
                    case 6 -> menuAcessos();
                    case 0 -> voltar = true;
                    default -> console.erro("Opcao invalida.");
                }
            } catch (br.com.redeacademia.exception.AcademiaException
                     | IllegalArgumentException | IllegalStateException e) {
                // Captura centralizada: regras de negocio e validacoes nao derrubam o sistema.
                console.erro(e.getMessage());
                console.pausar();
            }
        }
    }

    // ------------------------------------------------------------ Funcionarios

    private void menuFuncionarios() {
        console.titulo("Funcionarios");
        console.msg("1) Atribuir gerente (nivel rede)");
        console.msg("2) Contratar instrutor");
        console.msg("3) Contratar recepcionista");
        console.msg("4) Listar funcionarios (inclui o gerente)");
        console.msg("5) Atualizar funcionario");
        console.msg("6) Demitir funcionario");
        console.msg("7) Folha de pagamento");
        console.msg("8) Relatorios operacionais (polimorfico)");
        console.msg("0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> atribuirGerente();
            case 2 -> contratarInstrutor();
            case 3 -> contratarRecepcionista();
            case 4 -> listarFuncionarios();
            case 5 -> atualizarFuncionario();
            case 6 -> demitirFuncionario();
            case 7 -> console.msg("\n" + s.funcionarios.gerarFolhaPagamento(academia.getId()));
            case 8 -> console.msg("\n" + s.funcionarios.gerarRelatoriosOperacionais(academia.getId()));
            default -> { return; }
        }
        console.pausar();
    }

    private void atribuirGerente() {
        Gerente g = new Gerente(
                br.com.redeacademia.util.GeradorId.gerar("FUN"),
                console.lerTextoObrigatorio("Nome"),
                console.lerTextoObrigatorio("CPF"),
                console.lerTexto("E-mail"),
                console.lerTexto("Telefone"),
                console.lerTextoObrigatorio("Matricula funcional"),
                console.lerDouble("Salario"),
                academia.getId(),
                console.lerEnum("Nivel de acesso", NivelAcesso.class),
                console.lerDouble("Bonus de gestao (ex.: 0.25)"));
        s.funcionarios.contratarGerente(academia, g);
        console.sucesso("Gerente atribuido: " + g.getNome());
    }

    private void contratarInstrutor() {
        Gerente atuante = s.funcionarios.buscarGerenteDaAcademia(academia);
        Instrutor i = new Instrutor(
                br.com.redeacademia.util.GeradorId.gerar("FUN"),
                console.lerTextoObrigatorio("Nome"),
                console.lerTextoObrigatorio("CPF"),
                console.lerTexto("E-mail"),
                console.lerTexto("Telefone"),
                console.lerTextoObrigatorio("Matricula funcional"),
                console.lerDouble("Salario"),
                academia.getId(),
                console.lerTexto("CREF"),
                console.lerTexto("Especialidade"),
                console.lerInt("Carga horaria semanal"));
        s.funcionarios.contratarFuncionario(atuante, academia, i);
        console.sucesso("Instrutor contratado: " + i.getNome());
    }

    private void contratarRecepcionista() {
        Gerente atuante = s.funcionarios.buscarGerenteDaAcademia(academia);
        Recepcionista r = new Recepcionista(
                br.com.redeacademia.util.GeradorId.gerar("FUN"),
                console.lerTextoObrigatorio("Nome"),
                console.lerTextoObrigatorio("CPF"),
                console.lerTexto("E-mail"),
                console.lerTexto("Telefone"),
                console.lerTextoObrigatorio("Matricula funcional"),
                console.lerDouble("Salario"),
                academia.getId(),
                console.lerEnum("Turno", TurnoFuncionario.class),
                console.lerTexto("Senha de acesso"));
        s.funcionarios.contratarFuncionario(atuante, academia, r);
        console.sucesso("Recepcionista contratado: " + r.getNome());
    }

    private void listarFuncionarios() {
        List<Funcionario> funcionarios = s.funcionarios.listarPorAcademia(academia.getId());
        if (funcionarios.isEmpty()) {
            console.msg("Nenhum funcionario.");
            return;
        }
        for (Funcionario f : funcionarios) {
            String marca = f.getId().equals(academia.getGerenteId()) ? "   <== GERENTE DA UNIDADE" : "";
            console.msg(String.format("- [%s] %s | %s | salario R$ %,.2f%s",
                    f.getId(), f.getNome(), f.getTipo(), f.getSalario(), marca));
        }
    }

    private void atualizarFuncionario() {
        listarFuncionarios();
        Funcionario f = s.funcionarios.buscarPorId(console.lerTextoObrigatorio("ID do funcionario"));

        // Campos comuns a todos os funcionarios (o CPF e imutavel, por ser identidade).
        f.setNome(console.lerTextoOuManter("Nome", f.getNome()));
        f.setEmail(console.lerTextoOuManter("E-mail", f.getEmail()));
        f.setTelefone(console.lerTextoOuManter("Telefone", f.getTelefone()));
        f.setSalario(console.lerDoubleOuManter("Salario", f.getSalario()));

        // Campos especificos de cada cargo (inclui o gerente).
        if (f instanceof Gerente g) {
            g.setNivelAcesso(console.lerEnumOuManter("Nivel de acesso", NivelAcesso.class, g.getNivelAcesso()));
            g.setBonusGestao(console.lerDoubleOuManter("Bonus de gestao (ex.: 0.25)", g.getBonusGestao()));
        } else if (f instanceof Instrutor i) {
            i.setCref(console.lerTextoOuManter("CREF", i.getCref()));
            i.setEspecialidade(console.lerTextoOuManter("Especialidade", i.getEspecialidade()));
            i.setCargaHoraria(console.lerIntOuManter("Carga horaria semanal", i.getCargaHoraria()));
        } else if (f instanceof Recepcionista r) {
            r.setTurno(console.lerEnumOuManter("Turno", TurnoFuncionario.class, r.getTurno()));
            r.setSenhaAcesso(console.lerTextoOuManter("Senha de acesso", r.getSenhaAcesso()));
        }
        console.sucesso("Funcionario atualizado: " + f.getNome());
    }

    private void demitirFuncionario() {
        listarFuncionarios();
        Gerente atuante = s.funcionarios.buscarGerenteDaAcademia(academia);
        Funcionario alvo = s.funcionarios.buscarPorId(console.lerTextoObrigatorio("ID do funcionario a demitir"));
        s.funcionarios.demitir(atuante, academia, alvo);
        console.sucesso("Funcionario demitido: " + alvo.getNome());
    }

    // ------------------------------------------------------------ Clientes

    private void menuClientes() {
        console.titulo("Clientes");
        console.msg("1) Cadastrar  2) Listar  3) Buscar por CPF  4) Atualizar  5) Remover  0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> cadastrarCliente();
            case 2 -> listarClientes();
            case 3 -> buscarClientePorCpf();
            case 4 -> atualizarCliente();
            case 5 -> removerCliente();
            default -> { return; }
        }
        console.pausar();
    }

    private void cadastrarCliente() {
        Cliente c = s.clientes.cadastrar(
                academia.getId(),
                console.lerTextoObrigatorio("Nome"),
                console.lerTextoObrigatorio("CPF"),
                console.lerTexto("E-mail"),
                console.lerTexto("Telefone"),
                console.lerData("Data de nascimento"),
                console.lerTexto("Objetivo"),
                console.lerDouble("Peso (kg)"),
                console.lerDouble("Altura (m)"));
        console.sucesso(String.format("Cliente cadastrado: %s (IMC %.1f - %s)",
                c.getNome(), c.calcularIMC(), c.classificarImc()));
    }

    private void listarClientes() {
        List<Cliente> clientes = s.clientes.listarPorAcademia(academia.getId());
        if (clientes.isEmpty()) {
            console.msg("Nenhum cliente.");
            return;
        }
        for (Cliente c : clientes) {
            console.msg(String.format("- [%s] %s | CPF %s | %d anos | %s",
                    c.getId(), c.getNome(), c.getCpf(), c.calcularIdade(),
                    c.verificarAtivo() ? "ativo" : "inativo"));
        }
    }

    private void buscarClientePorCpf() {
        Cliente c = s.clientes.buscarPorCpf(console.lerTextoObrigatorio("CPF"));
        console.msg(String.format("%s | %s | IMC %.1f (%s)", c.getNome(), c.getEmail(),
                c.calcularIMC(), c.classificarImc()));
    }

    private void atualizarCliente() {
        listarClientes();
        Cliente c = s.clientes.buscarPorId(console.lerTextoObrigatorio("ID do cliente"));
        s.clientes.atualizar(
                c.getId(),
                console.lerTextoOuManter("Nome", c.getNome()),
                console.lerTextoOuManter("E-mail", c.getEmail()),
                console.lerTextoOuManter("Telefone", c.getTelefone()),
                console.lerTextoOuManter("Objetivo", c.getObjetivo()),
                console.lerDoubleOuManter("Peso (kg)", c.getPeso()),
                console.lerDoubleOuManter("Altura (m)", c.getAltura()));
        console.sucesso("Cliente atualizado.");
    }

    private void removerCliente() {
        listarClientes();
        s.clientes.remover(console.lerTextoObrigatorio("ID do cliente a remover"));
        console.sucesso("Cliente removido.");
    }

    // ------------------------------------------------------------ Planos

    private void menuPlanos() {
        console.titulo("Planos");
        console.msg("1) Criar  2) Listar  3) Atualizar  4) Remover  0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> criarPlano();
            case 2 -> listarPlanos();
            case 3 -> atualizarPlano();
            case 4 -> removerPlano();
            default -> { return; }
        }
        console.pausar();
    }

    private void criarPlano() {
        Gerente atuante = s.funcionarios.buscarGerenteDaAcademia(academia);
        Plano p = s.planos.criar(atuante, academia,
                console.lerTextoObrigatorio("Nome do plano"),
                console.lerDouble("Valor mensal"),
                console.lerInt("Duracao (meses)"));
        console.sucesso(String.format("Plano criado: %s (desconto %.0f%%)", p.getNome(), p.calcularDesconto() * 100));
    }

    private void listarPlanos() {
        List<Plano> planos = s.planos.listarPorAcademia(academia.getId());
        if (planos.isEmpty()) {
            console.msg("Nenhum plano.");
            return;
        }
        for (Plano p : planos) {
            console.msg(String.format("- [%s] %s | R$ %,.2f/mes | %d meses | total c/ desconto R$ %,.2f",
                    p.getId(), p.getNome(), p.getValorMensal(), p.getDuracaoMeses(),
                    p.calcularValorTotalComDesconto()));
        }
    }

    private void atualizarPlano() {
        listarPlanos();
        String id = console.lerTextoObrigatorio("ID do plano");
        Plano p = s.planos.buscarPorId(id); // valores atuais para pre-carregar
        s.planos.atualizar(id,
                console.lerTextoOuManter("Nome do plano", p.getNome()),
                console.lerDoubleOuManter("Valor mensal", p.getValorMensal()),
                console.lerIntOuManter("Duracao (meses)", p.getDuracaoMeses()));
        console.sucesso("Plano atualizado.");
    }

    private void removerPlano() {
        listarPlanos();
        Gerente atuante = s.funcionarios.buscarGerenteDaAcademia(academia);
        s.planos.remover(atuante, academia, console.lerTextoObrigatorio("ID do plano a remover"));
        console.sucesso("Plano removido.");
    }

    // ------------------------------------------------------ Matriculas/Pagamentos

    private void menuMatriculas() {
        console.titulo("Matriculas e Pagamentos");
        console.msg("1) Criar matricula (gera pagamento)");
        console.msg("2) Listar matriculas");
        console.msg("3) Registrar pagamento");
        console.msg("4) Ativar matricula");
        console.msg("5) Suspender matricula");
        console.msg("6) Cancelar matricula");
        console.msg("7) Renovar matricula");
        console.msg("8) Listar inadimplentes");
        console.msg("0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> criarMatricula();
            case 2 -> listarMatriculas();
            case 3 -> registrarPagamento();
            case 4 -> ativarMatricula();
            case 5 -> alterarStatusMatricula("suspender");
            case 6 -> alterarStatusMatricula("cancelar");
            case 7 -> renovarMatricula();
            case 8 -> console.msg("\n" + s.pagamentos.listarInadimplentes(academia.getId()));
            default -> { return; }
        }
        console.pausar();
    }

    private void criarMatricula() {
        listarClientes();
        String clienteId = console.lerTextoObrigatorio("ID do cliente");
        listarPlanos();
        String planoId = console.lerTextoObrigatorio("ID do plano");
        Matricula m = s.matriculas.criarMatricula(clienteId, planoId);
        console.sucesso("Matricula criada (" + m.getStatus() + "). Pagamento inicial gerado como PENDENTE.");
    }

    private void listarMatriculas() {
        List<Matricula> matriculas = s.matriculas.listarPorAcademia(academia.getId());
        if (matriculas.isEmpty()) {
            console.msg("Nenhuma matricula.");
            return;
        }
        for (Matricula m : matriculas) {
            String cliente = s.clientes.buscarPorId(m.getClienteId()).getNome();
            console.msg(String.format("- [%s] %s | status %s | vencimento %s",
                    m.getId(), cliente, m.getStatus(), m.getDataVencimento()));
            for (Pagamento p : s.pagamentos.listarPorMatricula(m.getId())) {
                console.msg(String.format("     pagamento [%s] %s R$ %,.2f venc %s",
                        p.getId(), p.getStatus(), p.getValor(), p.getDataVencimento()));
            }
        }
    }

    private void registrarPagamento() {
        listarMatriculas();
        s.pagamentos.registrarPagamento(console.lerTextoObrigatorio("ID do pagamento"));
        console.sucesso("Pagamento registrado (PAGO).");
    }

    private void ativarMatricula() {
        listarMatriculas();
        s.matriculas.ativarMatricula(console.lerTextoObrigatorio("ID da matricula"));
        console.sucesso("Matricula ATIVADA.");
    }

    private void alterarStatusMatricula(String acao) {
        listarMatriculas();
        String id = console.lerTextoObrigatorio("ID da matricula");
        if (acao.equals("suspender")) {
            s.matriculas.suspenderMatricula(id);
            console.sucesso("Matricula SUSPENSA.");
        } else {
            s.matriculas.cancelarMatricula(id);
            console.sucesso("Matricula CANCELADA.");
        }
    }

    private void renovarMatricula() {
        listarMatriculas();
        s.matriculas.renovarMatricula(console.lerTextoObrigatorio("ID da matricula"));
        console.sucesso("Matricula RENOVADA e ATIVADA.");
    }

    // ------------------------------------------------------------ Treinos

    private void menuTreinos() {
        console.titulo("Treinos");
        console.msg("1) Criar treino (instrutor)");
        console.msg("2) Adicionar exercicio");
        console.msg("3) Atualizar treino");
        console.msg("4) Remover exercicio");
        console.msg("5) Listar por cliente");
        console.msg("6) Remover treino");
        console.msg("0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> criarTreino();
            case 2 -> adicionarExercicio();
            case 3 -> atualizarTreino();
            case 4 -> removerExercicio();
            case 5 -> listarTreinosPorCliente();
            case 6 -> removerTreino();
            default -> { return; }
        }
        console.pausar();
    }

    private void criarTreino() {
        Instrutor instrutor = selecionarInstrutor();
        listarClientes();
        String clienteId = console.lerTextoObrigatorio("ID do cliente");
        Treino t = s.treinos.criarTreino(instrutor, clienteId,
                console.lerTextoObrigatorio("Nome do treino"),
                console.lerTexto("Objetivo"),
                console.lerEnum("Nivel", NivelTreino.class),
                console.lerInt("Duracao (minutos)"));
        console.sucesso("Treino criado: " + t.getId());
    }

    private void adicionarExercicio() {
        String treinoId = console.lerTextoObrigatorio("ID do treino");
        s.treinos.adicionarExercicio(treinoId,
                console.lerTextoObrigatorio("Nome do exercicio"),
                console.lerTexto("Grupo muscular"),
                console.lerInt("Series"),
                console.lerInt("Repeticoes"),
                console.lerDouble("Carga (kg)"));
        Treino t = s.treinos.buscar(treinoId);
        console.sucesso(String.format("Exercicio adicionado. Carga total do treino: %.1f", t.calcularCargaTotal()));
    }

    private void atualizarTreino() {
        Treino t = s.treinos.buscar(console.lerTextoObrigatorio("ID do treino"));
        s.treinos.atualizar(t.getId(),
                console.lerTextoOuManter("Nome do treino", t.getNome()),
                console.lerTextoOuManter("Objetivo", t.getObjetivo()),
                console.lerEnumOuManter("Nivel", NivelTreino.class, t.getNivel()),
                console.lerIntOuManter("Duracao (minutos)", t.getDuracaoMinutos()));
        console.sucesso("Treino atualizado.");
    }

    private void removerExercicio() {
        Treino t = s.treinos.buscar(console.lerTextoObrigatorio("ID do treino"));
        if (t.getExercicios().isEmpty()) {
            console.msg("Este treino nao tem exercicios.");
            return;
        }
        t.getExercicios().forEach(e -> console.msg("- " + e.getNome() + " (" + e.getGrupoMuscular() + ")"));
        s.treinos.removerExercicio(t.getId(), console.lerTextoObrigatorio("Nome do exercicio a remover"));
        console.sucesso(String.format("Exercicio removido. Carga total do treino: %.1f", t.calcularCargaTotal()));
    }

    private void listarTreinosPorCliente() {
        listarClientes();
        String clienteId = console.lerTextoObrigatorio("ID do cliente");
        List<Treino> treinos = s.treinos.listarPorCliente(clienteId);
        if (treinos.isEmpty()) {
            console.msg("Nenhum treino para este cliente.");
            return;
        }
        for (Treino t : treinos) {
            console.msg(String.format("- [%s] %s | %s | %d exercicios | carga total %.1f",
                    t.getId(), t.getNome(), t.getNivel(), t.getExercicios().size(), t.calcularCargaTotal()));
        }
    }

    private void removerTreino() {
        s.treinos.remover(console.lerTextoObrigatorio("ID do treino a remover"));
        console.sucesso("Treino removido.");
    }

    // ------------------------------------------------------------ Acessos

    private void menuAcessos() {
        console.titulo("Acessos (catraca)");
        console.msg("1) Registrar acesso  2) Listar acessos  0) Voltar");
        switch (console.lerInt("Opcao")) {
            case 1 -> registrarAcesso();
            case 2 -> listarAcessos();
            default -> { return; }
        }
        console.pausar();
    }

    private void registrarAcesso() {
        Recepcionista recepcionista = selecionarRecepcionista();
        listarClientes();
        String clienteId = console.lerTextoObrigatorio("ID do cliente");
        TipoAcesso tipo = console.lerEnum("Tipo", TipoAcesso.class);
        s.acessos.registrarAcesso(recepcionista, clienteId, tipo);
        console.sucesso("Acesso registrado.");
    }

    private void listarAcessos() {
        var acessos = s.acessos.listarPorAcademia(academia.getId());
        if (acessos.isEmpty()) {
            console.msg("Nenhum acesso registrado.");
            return;
        }
        acessos.forEach(a -> console.msg("- " + a));
    }

    // ------------------------------------------------------------ auxiliares

    private Instrutor selecionarInstrutor() {
        List<Funcionario> instrutores = s.funcionarios.listarPorAcademia(academia.getId()).stream()
                .filter(f -> f instanceof Instrutor).toList();
        if (instrutores.isEmpty()) {
            throw new br.com.redeacademia.exception.AcademiaException("Nenhum instrutor cadastrado nesta academia.");
        }
        instrutores.forEach(f -> console.msg(String.format("- [%s] %s", f.getId(), f.getNome())));
        Funcionario f = s.funcionarios.buscarPorId(console.lerTextoObrigatorio("ID do instrutor"));
        if (!(f instanceof Instrutor i)) {
            throw new br.com.redeacademia.exception.AcademiaException("O ID informado nao e de um instrutor.");
        }
        return i;
    }

    private Recepcionista selecionarRecepcionista() {
        List<Funcionario> recepcionistas = s.funcionarios.listarPorAcademia(academia.getId()).stream()
                .filter(f -> f instanceof Recepcionista).toList();
        if (recepcionistas.isEmpty()) {
            throw new br.com.redeacademia.exception.AcademiaException("Nenhuma recepcionista cadastrada nesta academia.");
        }
        recepcionistas.forEach(f -> console.msg(String.format("- [%s] %s", f.getId(), f.getNome())));
        Funcionario f = s.funcionarios.buscarPorId(console.lerTextoObrigatorio("ID da recepcionista"));
        if (!(f instanceof Recepcionista r)) {
            throw new br.com.redeacademia.exception.AcademiaException("O ID informado nao e de uma recepcionista.");
        }
        return r;
    }
}
