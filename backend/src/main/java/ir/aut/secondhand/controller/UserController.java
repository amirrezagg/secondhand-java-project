package ir.aut.secondhand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.LoginRequest;
import ir.aut.secondhand.dto.LoginResponse;
import ir.aut.secondhand.dto.RegisterRequest;
import ir.aut.secondhand.dto.UpdateUserRequest;
import ir.aut.secondhand.dto.UserProfileResponse;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.security.JwtUtil;
import ir.aut.secondhand.service.UserService;
import jakarta.validation.Valid;

/**
 * REST controller for user account management and authentication endpoints.
 * Handles registration, login, profile retrieval, and profile update flows.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Register a new user, then return the created profile representation.
     * This ensures the response exposes only profile-safe fields.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest user) {
        User registeredUser = userService.register(user);
        return ResponseEntity.ok(new UserProfileResponse(registeredUser));
    }

    /**
     * Authenticate a user and issue a JWT for subsequent requests.
     * Delegates credential validation to the service layer.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(user, token));
    }

    /**
     * Retrieve the current authenticated user's profile.
     * The service resolves the user from the active security context.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(new UserProfileResponse(currentUser));
    }

    /**
     * Apply profile updates for the authenticated user and return the updated profile.
     * Requests are validated before forwarding to the service layer.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateUserRequest request) {
        User updatedUser = userService.updateUserProfile(request);
        return ResponseEntity.ok(new UserProfileResponse(updatedUser));
    }
}
