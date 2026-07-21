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
import ir.aut.secondhand.frontend.dto.CreateAdvertisementRequest;
import ir.aut.secondhand.frontend.dto.AdvertisementResponse;
import ir.aut.secondhand.frontend.dto.ReviewAdvertisementRequest;
import ir.aut.secondhand.frontend.dto.CategoryResponse;
import ir.aut.secondhand.frontend.dto.LocationResponse;
import ir.aut.secondhand.frontend.dto.UpdateAdvertisementRequest;
import ir.aut.secondhand.frontend.dto.FavoritesResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.io.File;
import java.util.UUID;

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

    public AdvertisementResponse createAdvertisement(
            CreateAdvertisementRequest createAdvertisementRequest
    ) throws IOException, InterruptedException {

        String requestBody =
                objectMapper.writeValueAsString(
                        createAdvertisementRequest
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL + "/advertisements/"
                        )
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .POST(
                        HttpRequest.BodyPublishers.ofString(
                                requestBody
                        )
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
                    AdvertisementResponse.class
            );
        }

        throw new IOException(
                extractAdvertisementErrorMessage(
                        response.body(),
                        response.statusCode()
                )
        );
    }

    private String extractAdvertisementErrorMessage(
            String responseBody,
            int statusCode
    ) {
        try {
            var root = objectMapper.readTree(responseBody);

            var errorsNode = root.path("errors");

            if (errorsNode.isObject()
                    && errorsNode.fieldNames().hasNext()) {

                String firstField =
                        errorsNode.fieldNames().next();

                return errorsNode
                        .path(firstField)
                        .asText();
            }

            if (root.has("message")) {
                return root
                        .path("message")
                        .asText();
            }

        } catch (Exception ignored) {
        }

        return "Advertisement submission failed. Status: "
                + statusCode;
    }


    public List<AdvertisementResponse> getPendingAdvertisements()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/advertisements/pending"))
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    extractAdvertisementErrorMessage(
                            response.body(),
                            response.statusCode()
                    )
            );
        }

        var root = objectMapper.readTree(response.body());

        // Backend may return either a raw array or a wrapped object.
        var advertisementsNode = root;

        if (root.isObject()) {
            if (root.has("items")) {
                advertisementsNode = root.get("items");
            } else if (root.has("data")) {
                advertisementsNode = root.get("data");
            } else if (root.has("content")) {
                advertisementsNode = root.get("content");
            } else if (root.has("result")) {
                advertisementsNode = root.get("result");
            }
        }

        if (!advertisementsNode.isArray()) {
            throw new IOException(
                    "Unexpected pending advertisements response: "
                            + response.body()
            );
        }

        return objectMapper.readerForListOf(
                AdvertisementResponse.class
        ).readValue(advertisementsNode);
    }

    public AdvertisementResponse reviewAdvertisement(
            Long id,
            ReviewAdvertisementRequest request
    ) throws IOException, InterruptedException {

        String body =
                objectMapper.writeValueAsString(request);

        HttpRequest httpRequest =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                                + "/advertisements/"
                                                + id
                                                + "/review"
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer " + SessionManager.getToken()
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .PUT(
                                HttpRequest.BodyPublishers
                                        .ofString(body)
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        httpRequest,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() >= 200
                && response.statusCode() < 300) {

            return objectMapper.readValue(
                    response.body(),
                    AdvertisementResponse.class
            );
        }

        throw new IOException(response.body());
    }

    public List<AdvertisementResponse> getAdvertisements()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/advertisements"))
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(response.body());
        }

        var root = objectMapper.readTree(response.body());

        var items = root.get("items");

        return objectMapper.readerForListOf(
                AdvertisementResponse.class
        ).readValue(items);
    }

    public List<CategoryResponse> getCategories()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/categories"))
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not load categories. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        var root = objectMapper.readTree(response.body());
        var categoriesNode = root;

        if (root.isObject()) {

            if (root.has("items")) {
                categoriesNode = root.get("items");

            } else if (root.has("data")) {
                categoriesNode = root.get("data");

            } else if (root.has("content")) {
                categoriesNode = root.get("content");
            }
        }

        if (!categoriesNode.isArray()) {
            throw new IOException(
                    "Unexpected categories response: "
                            + response.body()
            );
        }

        return objectMapper.readerForListOf(
                CategoryResponse.class
        ).readValue(categoriesNode);
    }

    public void deleteAdvertisement(Long advertisementId)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL
                                        + "/advertisements/"
                                        + advertisementId
                        )
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .DELETE()
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not delete advertisement. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }
    }

    public List<LocationResponse> getLocations()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL + "/locations/tree"
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not load locations. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        var root = objectMapper.readTree(
                response.body()
        );

        var locationsNode = root;

        if (root.isObject()) {

            if (root.has("items")) {
                locationsNode = root.get("items");

            } else if (root.has("data")) {
                locationsNode = root.get("data");

            } else if (root.has("content")) {
                locationsNode = root.get("content");
            }
        }

        if (!locationsNode.isArray()) {

            throw new IOException(
                    "Unexpected locations response: "
                            + response.body()
            );
        }

        return objectMapper.readerForListOf(
                LocationResponse.class
        ).readValue(locationsNode);
    }


    public void uploadAdvertisementImages(
            Long advertisementId,
            List<File> files,
            int mainImageIndex
    ) throws IOException, InterruptedException {

        if (files == null || files.isEmpty()) {
            return;
        }

        String boundary =
                "----SecondHandBoundary" + UUID.randomUUID();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        for (File file : files) {

            String mimeType =
                    Files.probeContentType(file.toPath());

            if (mimeType == null) {

                String lowerName =
                        file.getName().toLowerCase();

                mimeType = lowerName.endsWith(".png")
                        ? "image/png"
                        : "image/jpeg";
            }

            output.write(
                    ("--" + boundary + "\r\n")
                            .getBytes(StandardCharsets.UTF_8)
            );

            output.write(
                    (
                            "Content-Disposition: form-data; "
                                    + "name=\"files\"; filename=\""
                                    + file.getName()
                                    + "\"\r\n"
                    ).getBytes(StandardCharsets.UTF_8)
            );

            output.write(
                    ("Content-Type: " + mimeType + "\r\n\r\n")
                            .getBytes(StandardCharsets.UTF_8)
            );

            output.write(
                    Files.readAllBytes(file.toPath())
            );

            output.write(
                    "\r\n".getBytes(StandardCharsets.UTF_8)
            );
        }

        output.write(
                ("--" + boundary + "\r\n")
                        .getBytes(StandardCharsets.UTF_8)
        );

        output.write(
                (
                        "Content-Disposition: form-data; "
                                + "name=\"mainImageIndex\"\r\n\r\n"
                ).getBytes(StandardCharsets.UTF_8)
        );

        output.write(
                String.valueOf(mainImageIndex)
                        .getBytes(StandardCharsets.UTF_8)
        );

        output.write(
                "\r\n".getBytes(StandardCharsets.UTF_8)
        );

        output.write(
                ("--" + boundary + "--\r\n")
                        .getBytes(StandardCharsets.UTF_8)
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL
                                        + "/advertisements/"
                                        + advertisementId
                                        + "/images"
                        )
                )
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .header(
                        "Content-Type",
                        "multipart/form-data; boundary="
                                + boundary
                )
                .POST(
                        HttpRequest.BodyPublishers.ofByteArray(
                                output.toByteArray()
                        )
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Image upload failed. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }
    }

    public List<AdvertisementResponse> getMyAdvertisements()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL + "/advertisements/my"
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not load your advertisements. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        var root =
                objectMapper.readTree(
                        response.body()
                );

        var advertisementsNode = root;

        if (root.isObject()) {

            if (root.has("items")) {
                advertisementsNode = root.get("items");

            } else if (root.has("data")) {
                advertisementsNode = root.get("data");

            } else if (root.has("content")) {
                advertisementsNode = root.get("content");
            }
        }

        if (!advertisementsNode.isArray()) {

            throw new IOException(
                    "Unexpected my advertisements response: "
                            + response.body()
            );
        }

        return objectMapper
                .readerForListOf(
                        AdvertisementResponse.class
                )
                .readValue(advertisementsNode);
    }

    public AdvertisementResponse markAdvertisementAsSold(
            Long advertisementId
    ) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        BASE_URL
                                + "/advertisements/"
                                + advertisementId
                                + "/sold"
                ))
                .header(
                        "Authorization",
                        "Bearer " + SessionManager.getToken()
                )
                .PUT(
                        HttpRequest.BodyPublishers.noBody()
                )
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not mark advertisement as sold. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        return objectMapper.readValue(
                response.body(),
                AdvertisementResponse.class
        );
    }

    public AdvertisementResponse updateAdvertisement(
            Long advertisementId,
            UpdateAdvertisementRequest updateRequest
    ) throws IOException, InterruptedException {

        String requestBody =
                objectMapper.writeValueAsString(
                        updateRequest
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                                + "/advertisements/"
                                                + advertisementId
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer "
                                        + SessionManager.getToken()
                        )
                        .header(
                                "Content-Type",
                                "application/json"
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not update advertisement. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        return objectMapper.readValue(
                response.body(),
                AdvertisementResponse.class
        );
    }

    public List<FavoritesResponse> getFavorites()
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL + "/favorites"
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not load favorites. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }

        var root =
                objectMapper.readTree(
                        response.body()
                );

        var favoritesNode = root;

        if (root.isObject()) {

            if (root.has("items")) {
                favoritesNode = root.get("items");

            } else if (root.has("data")) {
                favoritesNode = root.get("data");

            } else if (root.has("content")) {
                favoritesNode = root.get("content");

            } else if (root.has("result")) {
                favoritesNode = root.get("result");
            }
        }

        if (!favoritesNode.isArray()) {

            throw new IOException(
                    "Unexpected favorites response: "
                            + response.body()
            );
        }

        return objectMapper
                .readerForListOf(
                        FavoritesResponse.class
                )
                .readValue(favoritesNode);
    }

    public void toggleFavorite(
            Long advertisementId
    ) throws IOException, InterruptedException {

        if (advertisementId == null) {
            throw new IllegalArgumentException(
                    "Advertisement ID cannot be null."
            );
        }

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        BASE_URL
                                                + "/favorites/"
                                                + advertisementId
                                                + "/toggle"
                                )
                        )
                        .header(
                                "Authorization",
                                "Bearer "
                                        + SessionManager.getToken()
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

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IOException(
                    "Could not update favorite. Status: "
                            + response.statusCode()
                            + " Body: "
                            + response.body()
            );
        }
    }

}