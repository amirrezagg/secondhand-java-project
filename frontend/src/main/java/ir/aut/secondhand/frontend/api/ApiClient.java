package ir.aut.secondhand.frontend.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import ir.aut.secondhand.frontend.dto.LoginRequest;
import ir.aut.secondhand.frontend.dto.LoginResponse;
import ir.aut.secondhand.frontend.dto.RegisterRequest;
import ir.aut.secondhand.frontend.dto.RegisterResponse;
import ir.aut.secondhand.frontend.SessionManager;
import ir.aut.secondhand.frontend.dto.UserProfileResponse;
import ir.aut.secondhand.frontend.dto.UpdateUserRequest;
import ir.aut.secondhand.frontend.dto.AdminUsersPageResponse;
import ir.aut.secondhand.frontend.dto.AdminUserResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private static final String BASE_URL =
            "http://localhost:8080/api";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {

        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public LoginResponse login(
            String username,
            String password
    ) throws IOException, InterruptedException {

        LoginRequest loginRequest =
                new LoginRequest(username, password);

        String requestBody =
                objectMapper.writeValueAsString(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL + "/users/login"
                ))
                .header(
                        "Content-Type",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(requestBody)
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    LoginResponse.class
            );
        }

        throw new IOException(
                extractErrorMessage(response.body())
        );
    }

    public RegisterResponse register(
            String username,
            String password,
            String fullName,
            String phoneNumber,
            String email
    ) throws IOException, InterruptedException {

        RegisterRequest registerRequest =
                new RegisterRequest(
                        username,
                        password,
                        fullName,
                        phoneNumber,
                        email
                );

        String requestBody =
                objectMapper.writeValueAsString(registerRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL + "/users/register"
                ))
                .header(
                        "Content-Type",
                        "application/json"
                )
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(requestBody)
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    RegisterResponse.class
            );
        }

        throw new IOException(
                extractRegisterErrorMessage(response.body())
        );
    }

    private String extractErrorMessage(String responseBody) {

        try {

            return objectMapper
                    .readTree(responseBody)
                    .path("message")
                    .asText("Login failed.");

        } catch (Exception exception) {

            return "Login failed.";
        }
    }

    private String extractRegisterErrorMessage(
            String responseBody
    ) {

        try {

            var root =
                    objectMapper.readTree(responseBody);

            var errorsNode =
                    root.path("errors");

            if (errorsNode.has("username")) {
                return errorsNode
                        .path("username")
                        .asText();
            }

            if (errorsNode.has("email")) {
                return errorsNode
                        .path("email")
                        .asText();
            }

            if (errorsNode.has("phoneNumber")) {
                return errorsNode
                        .path("phoneNumber")
                        .asText();
            }

            return root
                    .path("message")
                    .asText("Registration failed.");

        } catch (Exception exception) {

            return "Registration failed.";
        }
    }

    public UserProfileResponse getProfile()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL + "/users/profile"
                ))
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    UserProfileResponse.class
            );
        }

        throw new IOException(
                extractErrorMessage(response.body())
        );
    }

    public UserProfileResponse updateProfile(
            String username,
            String fullName,
            String phoneNumber,
            String email
    ) throws IOException, InterruptedException {

        UpdateUserRequest updateUserRequest =
                new UpdateUserRequest(
                        username,
                        fullName,
                        phoneNumber,
                        email
                );

        String requestBody =
                objectMapper.writeValueAsString(updateUserRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL + "/users/profile"
                ))
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .PUT(
                        HttpRequest.BodyPublishers
                                .ofString(requestBody)
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    UserProfileResponse.class
            );
        }

        throw new IOException(
                extractRegisterErrorMessage(response.body())
        );
    }

    public AdminUsersPageResponse getAdminUsers(
            int page,
            int size
    ) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL
                                        + "/admin/users?page="
                                        + page
                                        + "&size="
                                        + size
                        )
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    AdminUsersPageResponse.class
            );
        }

        throw new IOException(
                extractErrorMessage(response.body())
        );
    }

    public AdminUserResponse toggleUserBlock(
            Long userId
    ) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL
                                        + "/admin/users/"
                                        + userId
                                        + "/toggle-block"
                        )
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .POST(
                        HttpRequest.BodyPublishers.noBody()
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            AdminUserResponse result =
                    new AdminUserResponse();

            boolean blocked =
                    objectMapper.readTree(response.body())
                            .path("isBlocked")
                            .asBoolean();

            result.setBlocked(blocked);

            return result;
        }

        throw new IOException(
                "Could not update user. Status: "
                        + response.statusCode()
                        + " Body: "
                        + response.body()
        );
    }

}