package ir.aut.secondhand.frontend;

public class AdminUser {

    private final String username;
    private final String email;
    private String status;

    public AdminUser(
            String username,
            String email,
            String status
    ) {
        this.username = username;
        this.email = email;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
