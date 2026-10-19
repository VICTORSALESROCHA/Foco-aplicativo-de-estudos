package br.com.foco;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("principal.fxml"));
        Scene scene = new Scene(loader.load(), 1120, 760);
        stage.setTitle("Foco • Controle de Estudos");
        stage.setMinWidth(920);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
