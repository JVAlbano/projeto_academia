package projeto_academia.model;

import java.time.LocalDate;
 
public class Cliente extends Pessoa {
    private LocalDate dataNascimento;
    private String observacoesSaude;
 
    public Cliente(String id, String nome, String cpf, String telefone, String email,
                   LocalDate dataNascimento, String observacoesSaude) {
        super(id, nome, cpf, telefone, email);
        this.dataNascimento = dataNascimento;
        this.observacoesSaude = observacoesSaude;
    }