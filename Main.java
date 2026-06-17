import enums.TipoFuncionario;
import model.Cliente;
import model.Filial;
import model.Funcionario;
import model.Plano;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Filial filial = new Filial("F1", "Academia Centro", "Rua Principal, 100", "(11) 1111-1111", 200);

        Plano plano = new Plano("P1", "Plano Anual", "Acesso completo por 12 meses",
                120.0, 12, true, false, filial.getId());

        Funcionario funcionario = new Funcionario("FU1", "Maria Souza", "111.111.111-11",
                "(11) 2222-2222", "maria@academia.com", TipoFuncionario.INSTRUTOR,
                2500.0, LocalDate.of(2023, 3, 1), "Musculação");

        Cliente cliente = new Cliente("C1", "João Silva", "222.222.222-22",
                "(11) 3333-3333", "joao@email.com", LocalDate.of(1995, 7, 20),
                "Nenhuma restrição");

        System.out.println(filial);
        System.out.println(funcionario);
        System.out.println(funcionario.calcularRemuneracao());
        System.out.println(cliente);
        System.out.printf("Plano %s - Valor total: R$%.2f%n", plano.getNome(), plano.calcularValorTotal());
        System.out.printf("Com 10%% de desconto: R$%.2f%n", plano.calcularValorComDesconto(10));
    }
}
