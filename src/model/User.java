package model;

public class User extends Entity {
    private String username;
    private String password;
    private String role;

    public User(int id, String username, String password, String role) {
        super(id);            // <-- вызываем конструктор Entity
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
