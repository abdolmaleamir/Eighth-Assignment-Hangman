package hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class Game {
    public static final String API_KEY = "CA0G6R1OO14uG3RjjZEWuw==SefOAV3bK4LRqV1T";
    private DatabaseManager dbManager;
    private UUID gameID;
    private String wordToGuess;
    private boolean didWin;
    private LocalDateTime startTime;
    private String gameTime;
    private int wrongGuesses;
    public Game() {
        wordToGuess = generateWordToGuess();
        gameID = UUID.randomUUID();
        startTime = LocalDateTime.now();
        wrongGuesses = 0;
        didWin = false;
        dbManager = DatabaseManager.getDb();
    }

    public Game(String gameID, String wordToGuess, int wrongGuesses, String gameTime, boolean didWin){
        this.gameID = UUID.fromString(gameID);
        this.wordToGuess = wordToGuess;
        this.wrongGuesses = wrongGuesses;
        this.gameTime = gameTime;
        this.didWin = didWin;
    }

    private String generateWordToGuess(){
        Random random = new Random();
        StringBuilder response = new StringBuilder();

        // Request a Random Animal
        String letters = "abcdefghijklmnopqrstuvwxyz";
        String randomLetter = String.valueOf(letters.charAt(random.nextInt(letters.length())));

        try{
            URL url = new URL("https://api.api-ninjas.com/v1/animals?name=" +
                    randomLetter + "&apikey=" + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-Api-Key", API_KEY);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
            } else {
                return "Error: " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }
        } catch (IOException e) {
            System.out.println("Error During Requesting For Animals Json Array: " + e.getMessage());
        }

        // Get a Random Json Object From Response
        JSONArray jsonArray = new JSONArray(response.toString());
        JSONObject randomAnimal = null;
        String animalName = "";
        // Get a Proper Name (Length be less than 10 And not contain -_ (space))
        do {
            int randomIndex = random.nextInt(jsonArray.length());
            randomAnimal = jsonArray.getJSONObject(randomIndex);
            animalName = randomAnimal.getString("name");

            System.out.println("Got a Random name.");
        } while (animalName.length() > 10 || animalName.contains("-_ "));

        System.out.println("Randomly selected animal: " + animalName);

        return animalName;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }
    public int getWrongGuesses() {
        return wrongGuesses;
    }
    public LocalDateTime getStartTime(){
        return this.startTime;
    }
    public String getGameTime(){
        return this.gameTime;
    }
    public boolean didWin(){
        return this.didWin;
    }
    public String getGameID(){
        return this.gameID.toString();
    }

    public void setDidWin(boolean didWin) {
        this.didWin = didWin;
    }
    public void setWrongGuesses(int wrongGuesses) {
        this.wrongGuesses = wrongGuesses;
    }

    public void endGame(String username){
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        this.gameTime = duration.getSeconds() + "s";

        if(dbManager.storeGame(this, username)) System.out.println("Game Data Stored Successfully.");
        else System.out.println("Failed To Store Game Data.");
    }

    public static void main(String[] args) {
        Game g = new Game();
        System.out.println(g.getWordToGuess());
    }
}
