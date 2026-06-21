package br.com.redeacademia.repository;

import br.com.redeacademia.util.Json;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio generico em memoria com persistencia em arquivo de texto JSON (um
 * arquivo por entidade). As subclasses descrevem apenas como converter a entidade
 * de/para {@code Map<String,Object>} e como obter seu id.
 *
 * <p>A leitura e a escrita usam os fluxos (streams) de caractere do pacote
 * {@code java.io}: {@link FileWriter}/{@link BufferedWriter} para gravar e
 * {@link FileReader}/{@link BufferedReader} para ler, sempre dentro de
 * try-with-resources (o recurso e fechado automaticamente) e tratando
 * {@link IOException} (excecao checada).</p>
 *
 * @param <T> tipo da entidade gerenciada.
 */
public abstract class Repositorio<T> {

    /** Diretorio onde os arquivos sao gravados. */
    public static final String DIR_DADOS = "data";

    protected final List<T> itens = new ArrayList<>();
    private final File arquivo;

    protected Repositorio(String nomeArquivo) {
        this.arquivo = new File(DIR_DADOS, nomeArquivo);
    }

    // -- Contrato das subclasses --
    protected abstract String getId(T item);

    protected abstract Map<String, Object> paraMapa(T item);

    protected abstract T deMapa(Map<String, Object> mapa);

    // -- CRUD em memoria --
    public void adicionar(T item) {
        itens.add(item);
    }

    public List<T> listar() {
        return new ArrayList<>(itens);
    }

    public Optional<T> buscarPorId(String id) {
        return itens.stream().filter(i -> getId(i).equals(id)).findFirst();
    }

    public boolean remover(String id) {
        return itens.removeIf(i -> getId(i).equals(id));
    }

    public int quantidade() {
        return itens.size();
    }

    // -- Persistencia (modelo de leitura/escrita visto em aula) --

    /** Le o arquivo (se existir), linha a linha, e popula a lista em memoria. */
    public void carregar() {
        if (!arquivo.exists()) {
            return;
        }
        StringBuilder conteudo = new StringBuilder();
        // try-with-resources: o BufferedReader (e o FileReader) sao fechados ao final.
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao carregar " + arquivo, e);
        }

        if (conteudo.toString().isBlank()) {
            return;
        }
        itens.clear();
        for (Map<String, Object> mapa : Json.parseListaDeObjetos(conteudo.toString())) {
            itens.add(deMapa(mapa));
        }
    }

    /** Serializa a lista em memoria e grava no arquivo de texto JSON. */
    public void salvar() {
        // Garante a existencia da pasta "data" antes de gravar.
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        List<Object> lista = new ArrayList<>();
        for (T item : itens) {
            lista.add(paraMapa(item));
        }

        // try-with-resources: o BufferedWriter (e o FileWriter) sao fechados ao final.
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {
            escritor.write(Json.escrever(lista));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar " + arquivo, e);
        }
    }
}
