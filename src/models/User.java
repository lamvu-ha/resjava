package models;

public class User {
    public int id;
    public String username;
    public String name;
    public String role; // customer, staff, admin
    public String password;
    public String phone;

    public User(int id, String username, String name, String role, String password, String phone) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.password = password;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " (" + username + ") - " + role;
    }
}
