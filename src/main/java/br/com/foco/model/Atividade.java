package br.com.foco.model;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
public final class Atividade {
    private final UUID id;
    private final String titulo;
    private final String disciplina;
    private final LocalDate prazo;
    private final int minutos;
    private final boolean concluida;

    public Atividade(UUID id, String titulo, String disciplina, LocalDate prazo,
                     int minutos, boolean concluida) {
        this.id = Objects.requireNonNull(id, "ID obrigatório.");
        this.titulo = validarTexto(titulo, "Título");
        this.disciplina = validarTexto(disciplina, "Disciplina");
        this.prazo = Objects.requireNonNull(prazo, "Prazo obrigatório.");
        if (minutos < 1 || minutos > 10080) {
            throw new IllegalArgumentException("Informe de 1 a 10080 minutos.");
        }
        this.minutos = minutos;
        this.concluida = concluida;
    }
    private static String validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank() || texto.trim().length() > 120) {
            throw new IllegalArgumentException(campo + " deve ter entre 1 e 120 caracteres.");
        }
        return texto.trim();
    }
    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDisciplina() { return disciplina; }
    public LocalDate getPrazo() { return prazo; }
    public int getMinutos() { return minutos; }
    public boolean isConcluida() { return concluida; }
    public String getSituacao() {
        if (concluida) { return "Concluída"; }
        if (prazo.isBefore(LocalDate.now())) { return "Atrasada"; }
        return "Pendente";
    }
    public Atividade alternarConclusao() {
        return new Atividade(id, titulo, disciplina, prazo, minutos, !concluida);
    }
}
