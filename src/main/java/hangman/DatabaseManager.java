package hangman;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseManager {
    private static DatabaseManager db = null;
    private Connection connection;
    private DatabaseManager(){
        // Establish the connection to database
        String url = "jdbc:postgresql://localhost:1234/database";
        String user = "postgres";
        String password = "1234";

        try {
            // This Line is Optional...
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Error During Getting Connection to Database: " + e.getMessage());
        }

    }

    public static DatabaseManager getDb(){
        if(db == null)
            db = new DatabaseManager();
        return db;
    }
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Error during Closing database Connection: " + e.getMessage());
            }
        }
    }

    public User getUser(String username) {
        String query = "SELECT * FROM UserInfo WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()){
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");

                statement.close();
                resultSet.close();
                return new User(username, name, password);
            }
        } catch (SQLException e) {
            System.out.println("Error in getUser: " + e.getMessage());
        }

        return null;
    }

    public boolean registerUser(String name, String username, String password) {
        String checkUserSql = "SELECT 1 FROM userinfo WHERE username = ?";
        String insertUserSql = "INSERT INTO userinfo (name, username, password) VALUES (?, ?, ?)";

        try (PreparedStatement checkUserStmt = connection.prepareStatement(checkUserSql);
             PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSql)) {

            // Check if user already exists
            checkUserStmt.setString(1, username);
            ResultSet rs = checkUserStmt.executeQuery();
            if (rs.next()) {
                return false; // User already exists
            }
            rs.close();

            // Insert new user
            insertUserStmt.setString(1, name);
            insertUserStmt.setString(2, username);
            insertUserStmt.setString(3, password);

            int affectedRows = insertUserStmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.out.println("Error in registering new user: " + e.getMessage());
            return false;
        }

    }

    public boolean storeGame(Game game, String username){
        String checkUserSql = "SELECT 1 FROM gameinfo WHERE gameid = ?";
        String insertUserSql = "INSERT INTO gameinfo (gameid, username, word, wrongguesses, gametime, win) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement checkGameStmt = connection.prepareStatement(checkUserSql);
             PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSql)) {

            // Check if game already exists
            checkGameStmt.setString(1, game.getGameID());
            ResultSet rs = checkGameStmt.executeQuery();
            if (rs.next()) {
                return false; // Game already exists
            }
            rs.close();

            // Insert new Game Data
            insertUserStmt.setString(1, game.getGameID());
            insertUserStmt.setString(2, username);
            insertUserStmt.setString(3, game.getWordToGuess());
            insertUserStmt.setInt(4, game.getWrongGuesses());
            insertUserStmt.setString(5, game.getGameTime());
            insertUserStmt.setBoolean(6, game.didWin());

            int affectedRows = insertUserStmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error in registering new user: " + e.getMessage());
            return false;
        }

    }

    public ArrayList<Game> getGamesOfUser(String username){
        ArrayList<Game> gamesOfUser = new ArrayList<>();

        String query = "SELECT * FROM gameinfo WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()){
                Game g = new Game(resultSet.getString("gameid"), resultSet.getString("word"), resultSet.getInt("wrongguesses"), resultSet.getString("gametime"), resultSet.getBoolean("win"));
                gamesOfUser.add(g);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error in getUser: " + e.getMessage());
        }

        return gamesOfUser;
    }

    public HashMap<String, Integer> getLeaderBoard(){
        HashMap<String, Integer> leaderboard = new HashMap<>();

        try (Statement statement = connection.createStatement()) {

            statement.execute("SELECT username, win FROM gameinfo");
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next()){
                String name = resultSet.getString("username");
                boolean win = resultSet.getBoolean("win");
                if (win){
                    if (leaderboard.containsKey(name)) leaderboard.replace(name, leaderboard.get(name) + 1);
                    else leaderboard.put(name, 1);
                }
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error in getUser: " + e.getMessage());
        }

        return leaderboard;
    }

}