package br.com.foco.service;
import br.com.foco.model.Atividade;
import br.com.foco.repository.AtividadeRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        return atividades.stream()
                .filter(atividade -> {
            boolean corresponde = atividade.getTitulo().toLowerCase(Locale.ROOT).contains(busca)
                || atividade.getDisciplina().toLowerCase(Locale.ROOT).contains(busca);
            boolean aceita = "Todas".equals(situacao)
                || ("Pendentes".equals(situacao) && !atividade.isConcluida())
                || ("Concluídas".equals(situacao) && atividade.isConcluida())
                || ("Atrasadas".equals(situacao) && !atividade.isConcluida()
                    && atividade.getPrazo().isBefore(LocalDate.now()));
            return  corresponde && aceita;
            })
                .toList();
    }
    public List<Atividade> ordenar(List<Atividade> origem, String ordem) {
        List<Atividade> resultado = new ArrayList<>(origem);
        if ("Título".equals(ordem)) {
            resultado.sort((a, b) ->
             a.getTitulo().compareToIgnoreCase(b.getTitulo()));
        } else {
            resultado.sort((a, b) ->
                a.getPrazo().compareTo(b.getPrazo()));
        }
        return resultado;
    }
    public long contarConcluidas() {
        return atividades.stream()
                .filter(atividade -> atividade.isConcluida())
                .count();
        }
    public long somarMinutosPendentes() {
        return atividades.stream()
                .filter(atividade -> !atividade.isConcluida())
                .mapToLong(atividade -> atividade.getMinutos())
                .sum();
    }
    public Map<String, Long> contarPorDisciplina() {
        return atividades.stream()
                .collect(Collectors.groupingBy(
                        atividade -> atividade.getDisciplina(),
                        () -> new TreeMap<>(),
                        Collectors.counting()
                ));
    }
    public List<String>listarTitulos(){
        return atividades.stream()
                .map(atividade -> atividade.getTitulo())
                .toList();
    }
}
