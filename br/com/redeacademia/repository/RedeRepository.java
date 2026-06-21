package br.com.redeacademia.repository;

import br.com.redeacademia.model.RedeAcademica;
import br.com.redeacademia.util.Json;
import br.com.redeacademia.util.MapUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Persistencia da rede (rede.json). Diferente dos demais repositorios, guarda um
 * UNICO objeto (singleton de negocio), nao uma lista. As academias da rede sao
 * persistidas separadamente pelo {@link AcademiaRepository}.
 *
 * <p>Usa os mesmos fluxos de {@code java.io} adotados no projeto:
 * {@link BufferedReader}/{@link FileReader} para ler e
 * {@link BufferedWriter}/{@link FileWriter} para gravar.</p>
 */
public class RedeRepository {

    private final File arquivo = new File(Repositorio.DIR_DADOS, "rede.txt");

    @SuppressWarnings("unchecked")
    public Optional<RedeAcademica> carregar() {
        if (!arquivo.exists()) {
            return Optional.empty();
        }
        StringBuilder conteudo = new StringBuilder();
        try (BufferedReader leitor = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao carregar " + arquivo, e);
        }

        if (conteudo.toString().isBlank()) {
            return Optional.empty();
        }
        Map<String, Object> m = (Map<String, Object>) Json.parse(conteudo.toString());
        return Optional.of(new RedeAcademica(
                MapUtil.str(m, "id"),
                MapUtil.str(m, "nome"),
                MapUtil.str(m, "cnpj"),
                MapUtil.str(m, "site")));
    }

    public void salvar(RedeAcademica rede) {
        File pasta = arquivo.getParentFile();
        if (pasta != null && !pasta.exists()) {
            pasta.mkdirs();
        }

        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rede.getId());
        m.put("nome", rede.getNome());
        m.put("cnpj", rede.getCnpj());
        m.put("site", rede.getSite());

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo))) {
            escritor.write(Json.escrever(m));
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar " + arquivo, e);
        }
    }
}
