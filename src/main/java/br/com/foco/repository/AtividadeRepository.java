package br.com.foco.repository;
import br.com.foco.model.Atividade;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public final class AtividadeRepository {
    private final Path arquivo;
    public AtividadeRepository(Path arquivo) { this.arquivo = arquivo.toAbsolutePath(); }
    public Path getArquivo() { return arquivo; }

    public List<Atividade> carregar() throws IOException {
        List<Atividade> atividades = new ArrayList<>();
        if (!Files.exists(arquivo)) { return atividades; }
        Properties dados = new Properties();
        try (InputStream entrada = Files.newInputStream(arquivo)) {
            dados.loadFromXML(entrada);
        }
        try {
            int quantidade = Integer.parseInt(dados.getProperty("quantidade"));
            if (quantidade < 0 || quantidade > 100000) {
                throw new IllegalArgumentException("Quantidade inválida.");
            }
            Set<UUID> ids = new HashSet<>();
            for (int i = 0; i < quantidade; i++) {
                String prefixo = i + ".";
                UUID id = UUID.fromString(dados.getProperty(prefixo + "id"));
                if (!ids.add(id)) { throw new IllegalArgumentException("ID duplicado."); }
                String conclusao = dados.getProperty(prefixo + "concluida");
                if (!"true".equals(conclusao) && !"false".equals(conclusao)) {
                    throw new IllegalArgumentException("Situação inválida.");
                }
                atividades.add(new Atividade(id,
                    dados.getProperty(prefixo + "titulo"),
                    dados.getProperty(prefixo + "disciplina"),
                    LocalDate.parse(dados.getProperty(prefixo + "prazo")),
                    Integer.parseInt(dados.getProperty(prefixo + "minutos")),
                    Boolean.parseBoolean(conclusao)));
            }
        } catch (RuntimeException erro) {
            throw new IOException("Arquivo de estudos inválido. O original foi preservado.", erro);
        }
        return atividades;
    }
    public void salvar(List<Atividade> atividades) throws IOException {
        Properties dados = new Properties();
        dados.setProperty("quantidade", Integer.toString(atividades.size()));
        for (int i = 0; i < atividades.size(); i++) {
            Atividade atividade = atividades.get(i);
            String prefixo = i + ".";
            dados.setProperty(prefixo + "id", atividade.getId().toString());
            dados.setProperty(prefixo + "titulo", atividade.getTitulo());
            dados.setProperty(prefixo + "disciplina", atividade.getDisciplina());
            dados.setProperty(prefixo + "prazo", atividade.getPrazo().toString());
            dados.setProperty(prefixo + "minutos", Integer.toString(atividade.getMinutos()));
            dados.setProperty(prefixo + "concluida", Boolean.toString(atividade.isConcluida()));
        }
        Files.createDirectories(arquivo.getParent());
        Path temporario = Files.createTempFile(arquivo.getParent(), "estudos-", ".tmp");
        try {
            try (OutputStream saida = Files.newOutputStream(temporario)) {
                dados.storeToXML(saida, "Foco - Controle de Estudos", "UTF-8");
            }
            try {
                Files.move(temporario, arquivo, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException erro) {
                Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporario); }
    }
}
