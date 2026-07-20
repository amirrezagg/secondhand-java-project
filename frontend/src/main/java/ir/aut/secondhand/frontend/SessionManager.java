package ir.aut.secondhand.frontend;

public final class SessionManager {

    private static String token;
    private static String username;
    private static String fullName;
    private static String role;

    private SessionManager() {
    }

    public static void startSession(
            String newToken,
            String newUsername,
            String newFullName,
            String newRole
    ) {
        token = newToken;
        username = newUsername;
        fullName = newFullName;
        role = newRole;
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getRole() {
        return role;
    }

    public static boolean isLoggedIn() {
        return token != null && !token.isBlank();
    }

    public static void clearSession() {
        token = null;
        username = null;
        fullName = null;
        role = null;
    }
}
