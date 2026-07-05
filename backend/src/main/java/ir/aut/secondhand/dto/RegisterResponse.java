package ir.aut.secondhand.dto;

import ir.aut.secondhand.model.User;

public class RegisterResponse {

    private Long userId;
    private String username;
    private User.Role role;

    public RegisterResponse(Long userId, String username, User.Role role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User.Role getRole() {
        return role;
    }

    public void setRole(User.Role role) {
        this.role = role;
    }
}
