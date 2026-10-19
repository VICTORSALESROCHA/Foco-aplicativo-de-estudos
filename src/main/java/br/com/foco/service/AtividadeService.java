package br.com.foco.service;
import br.com.foco.model.Atividade;
import br.com.foco.repository.AtividadeRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public final class AtividadeService {
    private final AtividadeRepository repository;
    private List<Atividade> atividades = new ArrayList<>();
    public AtividadeService(AtividadeRepository repository) { this.repository = repository; }
    public void carregar() throws IOException { atividades = repository.carregar(); }
    public List<Atividade> listar() { return new ArrayList<>(atividades); }
    public void salvar(Atividade atividade) throws IOException {
        List<Atividade> novas = listar();
        int indice = localizar(atividade.getId());
        if (indice < 0) { novas.add(atividade); }
        else { novas.set(indice, atividade); }
        persistir(novas);
    }
    public void excluir(UUID id) throws IOException {
        int indice = localizar(id);
        if (indice < 0) { throw new IllegalArgumentException("Atividade não encontrada."); }
        List<Atividade> novas = listar();
        novas.remove(indice);
        persistir(novas);
    }
    private void persistir(List<Atividade> novas) throws IOException {
        repository.salvar(novas);
        atividades = novas;
    }
    private int localizar(UUID id) {
        for (int i = 0; i < atividades.size(); i++) {
            if (atividades.get(i).getId().equals(id)) { return i; }
        }
        return -1;
    }
    public List<Atividade> pesquisar(String texto, String situacao) {
        List<Atividade> resultado = new ArrayList<>();
        String busca = texto.trim().toLowerCase(Locale.ROOT);
        for (Atividade atividade : atividades) {
            boolean corresponde = atividade.getTitulo().toLowerCase(Locale.ROOT).contains(busca)
                || atividade.getDisciplina().toLowerCase(Locale.ROOT).contains(busca);
            boolean aceita = "Todas".equals(situacao)
                || ("Pendentes".equals(situacao) && !atividade.isConcluida())
                || ("Concluídas".equals(situacao) && atividade.isConcluida())
                || ("Atrasadas".equals(situacao) && !atividade.isConcluida()
                    && atividade.getPrazo().isBefore(LocalDate.now()));
            if (corresponde && aceita) { resultado.add(atividade); }
        }
        return resultado;
    }
    public List<Atividade> ordenar(List<Atividade> origem, String ordem) {
        List<Atividade> resultado = new ArrayList<>(origem);
        if ("Título".equals(ordem)) {
            resultado.sort(new Comparator<Atividade>() {
                @Override
                public int compare(Atividade a, Atividade b) {
                    return a.getTitulo().compareToIgnoreCase(b.getTitulo());
                }
            });
        } else {
            resultado.sort(new Comparator<Atividade>() {
                @Override
                public int compare(Atividade a, Atividade b) {
                    return a.getPrazo().compareTo(b.getPrazo());
                }
            });
        }
        return resultado;
    }
    public int contarConcluidas() {
        int quantidade = 0;
        for (Atividade atividade : atividades) {
            if (atividade.isConcluida()) { quantidade++; }
        }
        return quantidade;
    }
    public long somarMinutosPendentes() {
        long minutos = 0;
        for (Atividade atividade : atividades) {
            if (!atividade.isConcluida()) { minutos += atividade.getMinutos(); }
        }
        return minutos;
    }
    public Map<String, Integer> contarPorDisciplina() {
        Map<String, Integer> resultado = new TreeMap<>();
        for (Atividade atividade : atividades) {
            String disciplina = atividade.getDisciplina();
            Integer quantidade = resultado.get(disciplina);
            if (quantidade == null) { quantidade = 0; }
            resultado.put(disciplina, quantidade + 1);
        }
        return resultado;
    }
}
