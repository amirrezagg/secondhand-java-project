package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.User;

public class LoginResponse {

    private String username;
    private String fullName;
    private User.Role role;
    private String token;

    public LoginResponse(String username, String fullName, User.Role role, String token) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;

    }
}
