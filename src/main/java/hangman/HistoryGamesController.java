package hangman;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class HistoryGamesController {
    private User user;
    private DatabaseManager dbManager;
    @FXML
    public Label titleLabel;
    @FXML
    public VBox historyContainer;

    @FXML
    public void initialize() {
        dbManager = DatabaseManager.getDb();
    }

    public void createHistory(){
        for(Game g: dbManager.getGamesOfUser(this.user.getUsername())){
            HBox hBox = new HBox();
            hBox.getStyleClass().add("gameContainer");
            Label l1 = new Label("Game ID: " + g.getGameID());
            Label l2 = new Label("Word : " + g.getWordToGuess());
            Label l3 = new Label("Wrong Guesses : " + g.getWrongGuesses());
            Label l4 = new Label("Time : " + g.getGameTime());
            Label l5 = new Label("Win : " + (g.didWin() ? "Yes" : "No"));
            VBox.setMargin(hBox, new Insets(10, 0, 0, 0));

            hBox.getChildren().addAll(l1, l2, l3, l4 ,l5);

            historyContainer.getChildren().add(hBox);
        }
    }

    public void createLeaderboard(){
        System.out.println("Creating Leaderboard");
        HashMap<String, Integer> leaderboard = dbManager.getLeaderBoard();
        leaderboard = (HashMap<String, Integer>) sortByValue(leaderboard);

        leaderboard.forEach((username, wins) -> {
            // System.out.println(username + ": " + wins);
            HBox hBox = new HBox();
            hBox.getStyleClass().add("gameContainer");
            Label l1 = new Label("Username: " + username);
            Label l2 = new Label("Wins : " + wins);
            VBox.setMargin(hBox, new Insets(10, 0, 0, 0));

            hBox.getChildren().addAll(l1, l2);

            historyContainer.getChildren().add(hBox);
        });
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public void backToMenu(ActionEvent actionEvent) {
        // Load the Menu View
        FXMLLoader fxmlLoader = new FXMLLoader(HangmanApp.class.getResource("menu-view.fxml"));
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            System.out.println("Error While Getting Back to Menu: " + e.getMessage());
        }

        MenuController controller = fxmlLoader.getController();
        controller.backToManu(this.user);

        Scene scene = new Scene(root, 700, 400);
        scene.getStylesheets().add(getClass().getResource("styles/hangman-menupage.css").toExternalForm());

        Stage appStage = (Stage) (titleLabel).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }

    public void setUser(User user){
        this.user = user;
        titleLabel.setText("History of Games Played By: " + user.getUsername());
        createHistory();
    }

    public void initLeaderboard(User user){
        this.user = user;
        titleLabel.setText("Leaderboard");
        createLeaderboard();
    }

}





