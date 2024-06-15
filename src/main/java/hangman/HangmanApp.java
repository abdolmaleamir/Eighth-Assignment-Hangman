package hangman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HangmanApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HangmanApp.class.getResource("menu-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        scene.getStylesheets().add(getClass().getResource("styles/hangman-menupage.css").toExternalForm());

        stage.setTitle("Hangman");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}