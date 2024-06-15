package hangman;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.Duration;

import java.io.IOException;
import java.time.LocalDateTime;

public class HangmanController {

    private User player;
    private Game game;
    private int correctGuesses;
    private Timeline timeline;

    @FXML
    public HBox rootContainer;
    @FXML
    public VBox hangSceneContainer;
    @FXML
    public HBox answerContainer;
    @FXML
    public GridPane keysGrid;
    @FXML
    public Label timeLabel;
    @FXML
    public Label usernameLabel;
    @FXML
    public Label nameLabel;

    // an Array which contains each WordToGuess letters Label
    private Label[] answerLetters;

    @FXML
    private SVGPath hangmanSvg;

    private final String[] hangmanStages = {
            "",
            "M 50,80 L 100,80",
            "M 50,80 L 100,80 M 50,190 L 130,190",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0 M 130,70 L 130,130",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0 M 130,70 L 130,130 M 130,70 L 90,90",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0 M 130,70 L 130,130 M 130,70 L 90,90 M 130,70 L 170,90",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0 M 130,70 L 130,130 M 130,70 L 90,90 M 130,70 L 170,90 M 130,130 L 90,170",
            "M 50,190 L 130,190 M 90,10 L 90,50 M 50,10 L 130,10 M 90,10 L 90,30 M 130,50 m -20,0 a 20,20 0 1,0 40,0 a 20,20 0 1,0 -40,0 M 130,70 L 130,130 M 130,70 L 90,90 M 130,70 L 170,90 M 130,130 L 90,170 M 130,130 L 170,170",
    };

    public void initialize(){
        // Start a new Game
        this.game = new Game();
        System.out.println(this.game.getWordToGuess());
        correctGuesses = 0;

        // Start The Timer
        timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1), event -> updateTimer())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


        // Make The Input Keys.
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int column = 0;
        int row = 0;
        for (char letter : letters.toCharArray()) {
            Button button = new Button(String.valueOf(letter));
            button.getStyleClass().add("inpLetterBtn");
            button.setOnAction(e -> handleLetterClick(e, letter));
            keysGrid.add(button, column, row);

            column++;
            if (column == 9) {
                column = 0;
                row++;
            }
        }
        updateHangman();

        // Initialize the answerLetters Array Which Contains each answer Label.
        answerLetters = new Label[this.game.getWordToGuess().length()];

        // Set The WordToGuess Placeholder.
        for (int i = 0; i < this.game.getWordToGuess().length(); i++){
            Label label = new Label();
            label.getStyleClass().add("ansLetter");
            answerLetters[i] = label;
            answerContainer.getChildren().add(label);
        }

    }

    private void updateTimer() {
        Duration duration = Duration.between(this.game.getStartTime(), LocalDateTime.now());
        long seconds = duration.getSeconds();
        timeLabel.setText("Time: " + seconds + " seconds");
    }

    // Handler For Input Letter (Checking if WordToGuess Contains The Input Key)
    private void handleLetterClick(ActionEvent ae, char letter) {
        // Disable the Current Input key
        Button btn = (Button) ae.getSource();
        btn.setDisable(true);

        // Loop Through WordToGuess To See if it finds a match
        boolean foundAMatch = false;

        for (int i = 0; i < this.game.getWordToGuess().length(); i++) {
            if (Character.toUpperCase(this.game.getWordToGuess().charAt(i)) == letter){
                foundAMatch = true;
                answerLetters[i].setText(String.valueOf(letter));
                correctGuesses++;
            }
        }

        // Game Has Finished!
        if(correctGuesses == this.game.getWordToGuess().length())
            showGameOver(true);

        // If The Input Letter Wasn't a match, ++WrongGuesses
        if (!foundAMatch) {
            this.game.setWrongGuesses(this.game.getWrongGuesses() + 1);
            updateHangman();
        }
    }

    public void updateHangman(){
        if (this.game.getWrongGuesses() == 10) {
            showWholeWordToGuess();
            showGameOver(false);
            return;
        }
        hangmanSvg.setContent(hangmanStages[this.game.getWrongGuesses()]);
    }

    private void showWholeWordToGuess() {
        for (int i = 0; i < this.game.getWordToGuess().length(); i++){
            if (answerLetters[i].getText().isEmpty()){
                answerLetters[i].setText(String.valueOf(this.game.getWordToGuess().charAt(i)).toUpperCase());
                answerLetters[i].getStyleClass().add("red");
            }
        }
    }

    private void showGameOver(boolean didWin) {
        this.game.setDidWin(didWin);
        timeline.stop();
        this.game.endGame(player.getUsername());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(didWin ? "Congrats!" : "Game Over!");
        alert.setHeaderText(didWin ? "Keep it up!" : "Don't Lose Hope!");
        alert.setContentText("Getting Back to Menu.");

        // Block input to other windows
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait().ifPresent(response -> {
            // Return To Main Menu

            // Load the Menu View
            FXMLLoader fxmlLoader = new FXMLLoader(HangmanApp.class.getResource("menu-view.fxml"));
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                System.out.println("Error While Getting Back to Menu: " + e.getMessage());
            }

            MenuController controller = fxmlLoader.getController();
            controller.backToManu(this.player);

            Scene scene = new Scene(root, 700, 400);
            scene.getStylesheets().add(getClass().getResource("styles/hangman-menupage.css").toExternalForm());

            Stage appStage = (Stage) (answerContainer).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();

        });
    }

    public void setUser(User user){
        this.player = user;
        usernameLabel.setText("Username: " + user.getUsername());
        nameLabel.setText("Name: " + user.getName());
    }
}