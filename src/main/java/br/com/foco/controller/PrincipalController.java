package br.com.foco.controller;
import br.com.foco.model.Atividade;
import br.com.foco.repository.AtividadeRepository;
import br.com.foco.service.AtividadeService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

public final class PrincipalController {
    @FXML private VBox conteudo;
    @FXML private TextField tituloField;
    @FXML private TextField disciplinaField;
    @FXML private TextField minutosField;
    @FXML private DatePicker prazoPicker;
    @FXML private TextField buscaField;
    @FXML private ComboBox<String> situacaoCombo;
    @FXML private ComboBox<String> ordemCombo;
    @FXML private TableView<Atividade> tabela;
    @FXML private TableColumn<Atividade, String> tituloCol;
    @FXML private TableColumn<Atividade, String> disciplinaCol;
    @FXML private TableColumn<Atividade, LocalDate> prazoCol;
    @FXML private TableColumn<Atividade, Integer> minutosCol;
    @FXML private TableColumn<Atividade, String> situacaoCol;
    @FXML private Label totalLabel;
    @FXML private Label concluidasLabel;
    @FXML private Label minutosLabel;
    @FXML private Label mensagemLabel;
    @FXML private Label formularioLabel;
    @FXML private ProgressBar progresso;
    private final AtividadeRepository repository = new AtividadeRepository(
        Path.of(System.getProperty("user.home"), ".foco-estudos", "atividades.xml"));
    private final AtividadeService service = new AtividadeService(repository);
    private Atividade emEdicao;

    @FXML
    private void initialize() {
        tituloCol.setCellValueFactory(new PropertyValueFactory<Atividade, String>("titulo"));
        disciplinaCol.setCellValueFactory(new PropertyValueFactory<Atividade, String>("disciplina"));
        prazoCol.setCellValueFactory(new PropertyValueFactory<Atividade, LocalDate>("prazo"));
        minutosCol.setCellValueFactory(new PropertyValueFactory<Atividade, Integer>("minutos"));
        situacaoCol.setCellValueFactory(new PropertyValueFactory<Atividade, String>("situacao"));
        tabela.setPlaceholder(new Label("Nenhuma atividade para exibir."));
        situacaoCombo.getItems().addAll("Todas", "Pendentes", "Concluídas", "Atrasadas");
        situacaoCombo.setValue("Todas");
        ordemCombo.getItems().addAll("Prazo", "Título");
        ordemCombo.setValue("Prazo");
        novo();
        try {
            service.carregar();
            atualizar();
            mensagemLabel.setText("Dados: " + repository.getArquivo());
        } catch (IOException erro) {
            conteudo.setDisable(true);
            mensagemLabel.setText("Falha ao carregar. Feche o aplicativo e verifique o arquivo: "
                + repository.getArquivo());
            alerta(Alert.AlertType.ERROR, "Não foi possível carregar", erro.getMessage());
        }
    }
    @FXML
    private void salvar() {
        try {
            if (prazoPicker.getValue() == null) {
                throw new IllegalArgumentException("Selecione um prazo no calendário.");
            }
            int minutos;
            try { minutos = Integer.parseInt(minutosField.getText().trim()); }
            catch (NumberFormatException erro) {
                throw new IllegalArgumentException("Os minutos devem ser um número inteiro.");
            }
            UUID id = UUID.randomUUID();
            boolean concluida = false;
            if (emEdicao != null) {
                id = emEdicao.getId();
                concluida = emEdicao.isConcluida();
            }
            service.salvar(new Atividade(id, tituloField.getText(), disciplinaField.getText(),
                prazoPicker.getValue(), minutos, concluida));
            novo();
            atualizar();
            mensagemLabel.setText("Atividade salva. Os filtros atuais continuam aplicados.");
        } catch (IllegalArgumentException erro) {
            alerta(Alert.AlertType.WARNING, "Revise os campos", erro.getMessage());
        } catch (IOException erro) { erroGravacao(erro); }
    }
    @FXML
    private void novo() {
        emEdicao = null;
        tituloField.clear(); disciplinaField.clear(); minutosField.clear();
        prazoPicker.setValue(LocalDate.now());
        formularioLabel.setText("Nova atividade");
        tabela.getSelectionModel().clearSelection();
    }
    @FXML
    private void editar() {
        Atividade atividade = selecionada();
        if (atividade == null) { return; }
        emEdicao = atividade;
        tituloField.setText(atividade.getTitulo());
        disciplinaField.setText(atividade.getDisciplina());
        minutosField.setText(Integer.toString(atividade.getMinutos()));
        prazoPicker.setValue(atividade.getPrazo());
        formularioLabel.setText("Editar atividade");
    }
    @FXML
    private void alternarConclusao() {
        Atividade atividade = selecionada();
        if (atividade == null) { return; }
        try {
            service.salvar(atividade.alternarConclusao());
            novo(); atualizar();
            mensagemLabel.setText("Situação atualizada e salva.");
        } catch (IOException erro) { erroGravacao(erro); }
    }
    @FXML
    private void excluir() {
        Atividade atividade = selecionada();
        if (atividade == null) { return; }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
            "Excluir a atividade: " + atividade.getTitulo() + "?", ButtonType.YES, ButtonType.NO);
        confirmacao.setHeaderText("Confirmar exclusão");
        Optional<ButtonType> resposta = confirmacao.showAndWait();
        if (!resposta.isPresent() || resposta.get() != ButtonType.YES) { return; }
        try {
            service.excluir(atividade.getId());
            novo(); atualizar();
            mensagemLabel.setText("Atividade excluída.");
        } catch (IOException erro) { erroGravacao(erro); }
    }
    @FXML
    private void atualizar() {
        tabela.getItems().setAll(service.ordenar(
            service.pesquisar(buscaField.getText(), situacaoCombo.getValue()), ordemCombo.getValue()));
        int total = service.listar().size();
        long concluidas = service.contarConcluidas();
        totalLabel.setText(Long.toString(total));
        concluidasLabel.setText(Long.toString(concluidas));
        minutosLabel.setText(Long.toString(service.somarMinutosPendentes()));
        if (total == 0) { progresso.setProgress(0); }
        else { progresso.setProgress((double) concluidas / total); }
    }
    @FXML
    private void resumo() {
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<String, Long> entrada :
                service.contarPorDisciplina().entrySet()) {
            texto.append(entrada.getKey()).append(": ").append(entrada.getValue()).append(" atividade(s)\n");
        }
        if (texto.length() == 0) { texto.append("Nenhuma atividade cadastrada."); }
        TextArea area = new TextArea(texto.toString());
        area.setEditable(false); area.setWrapText(true); area.setPrefSize(420, 300);
        Alert dialogo = new Alert(Alert.AlertType.INFORMATION);
        dialogo.setTitle("Resumo"); dialogo.setHeaderText("Atividades por disciplina — todos os registros");
        dialogo.getDialogPane().setContent(area); dialogo.showAndWait();
    }
    private Atividade selecionada() {
        Atividade atividade = tabela.getSelectionModel().getSelectedItem();
        if (atividade == null) { alerta(Alert.AlertType.WARNING, "Selecione uma atividade", "Clique em uma linha da tabela primeiro."); }
        return atividade;
    }
    private void erroGravacao(IOException erro) {
        alerta(Alert.AlertType.ERROR, "Não foi possível salvar", "A alteração não foi aplicada. " + erro.getMessage());
    }
    private void alerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Foco"); alerta.setHeaderText(titulo); alerta.setContentText(texto);
        alerta.showAndWait();
    }
}
