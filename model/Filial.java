package projeto_academia.model;
 
import java.util.ArrayList;
import java.util.List;
 
public class Filial {
    private String id;
    private String nome;
    private String endereco;
    private String telefone;
    private int capacidadeMaxima;
    private boolean ativa;
 
    private List<String> planoIds = new ArrayList<>();
    private List<String> funcionarioIds = new ArrayList<>();
    private List<String> clienteIds = new ArrayList<>();
 
    public Filial(String id, String nome, String endereco, String telefone, int capacidadeMaxima) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativa = true;
    }
 
    public Filial(String id, String nome, String endereco, String telefone, int capacidadeMaxima, boolean ativa) {
        this(id, nome, endereco, telefone, capacidadeMaxima);
        this.ativa = ativa;
    }