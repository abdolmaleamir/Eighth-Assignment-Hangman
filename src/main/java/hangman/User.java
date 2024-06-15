package hangman;

public class User {
    private final String username;
    private String name;
    private String password;

    public User(String username, String name, String password){
        this.username = username;
        this.name = name;
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }
}
