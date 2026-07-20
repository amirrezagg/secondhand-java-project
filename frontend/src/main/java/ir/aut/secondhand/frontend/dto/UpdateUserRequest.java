package ir.aut.secondhand.frontend.dto;

public class UpdateUserRequest {

    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;

    public UpdateUserRequest() {
    }

    public UpdateUserRequest(
            String username,
            String fullName,
            String phoneNumber,
            String email
    ) {
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
