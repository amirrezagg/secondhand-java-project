package ir.aut.secondhand.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.RegisterRequest;
import ir.aut.secondhand.dto.UpdateUserRequest;
import ir.aut.secondhand.exception.DuplicateResourceException;
import ir.aut.secondhand.exception.UserIsNotAuthenticatedException;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.UserRepository;
import ir.aut.secondhand.util.PhoneNumberValidationUtil;

/**
 * Service layer for user management in the second-hand marketplace.
 * Handles user registration, authentication, profile retrieval, and profile updates,
 * ensuring data integrity and enforcing business rules for user operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user in the system with the provided registration details.
     * Validates uniqueness of username, phone number, and email before creating the account.
     * Normalizes the phone number format and encodes the password for secure storage.
     *
     * @param requestedUser the registration request containing user details
     * @return the persisted {@link User} entity
     * @throws DuplicateResourceException if username, phone number, or email already exists in the system
     */
    public User register(RegisterRequest requestedUser) {
        // Normalize phone number to a consistent format before validation and storage
        requestedUser.setPhoneNumber(PhoneNumberValidationUtil.normalizePhoneNumber(requestedUser.getPhoneNumber()));

        // Check uniqueness of critical user identifiers
        if (userRepository.existsByUsername(requestedUser.getUsername())) {
            throw new DuplicateResourceException("username");
        }
        if (userRepository.existsByPhoneNumber(requestedUser.getPhoneNumber())) {
            throw new DuplicateResourceException("phone number");
        }
        if (userRepository.existsByEmail(requestedUser.getEmail())) {
            throw new DuplicateResourceException("email");
        }

        // Build the new user entity with default role and active status
        User user = new User();
        user.setUsername(requestedUser.getUsername());
        user.setFullName(requestedUser.getFullName());
        user.setEmail(requestedUser.getEmail());
        user.setPhoneNumber(requestedUser.getPhoneNumber());
        user.setRole(User.Role.USER);
        user.setBlocked(false);
        user.setPassword(passwordEncoder.encode(requestedUser.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Authenticates a user by username and password for login purposes.
     * Verifies credentials and checks if the user is blocked before granting access.
     *
     * @param username the username provided during login
     * @param password the raw password provided during login
     * @return the authenticated {@link User} entity if credentials are valid
     * @throws BadCredentialsException if the username or password is incorrect
     * @throws IllegalArgumentException if the user account is blocked
     */
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Prevent blocked users from accessing the system
        if(user.isBlocked()){
            throw new IllegalArgumentException("You have been blocked by admin");
        }
        // Verify password matches the stored encoded version
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return user;
    }

    /**
     * Retrieves the currently authenticated user from the security context.
     *
     * @return the authenticated {@link User} entity
     * @throws UserIsNotAuthenticatedException if no user is authenticated or the session is anonymous
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Validate that an authenticated user session exists
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserIsNotAuthenticatedException();
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(UserIsNotAuthenticatedException::new);
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Allows updating email, full name, and password with uniqueness validation
     * for email if changed. Only non-empty fields from the request are applied.
     *
     * @param updatedUser the request containing the fields to update
     * @return the updated and persisted {@link User} entity
     * @throws DuplicateResourceException if the new email is already used by another user
     */
    public User updateUserProfile(UpdateUserRequest updatedUser) {
        User currentUser = getCurrentUser();

        // Update email only if provided and different from current, ensuring uniqueness
        String updatedUserEmail = updatedUser.getEmail();
        if (updatedUserEmail != null && !updatedUserEmail.isEmpty() && !currentUser.getEmail().equals(updatedUserEmail)) {
            userRepository.findByEmail(updatedUserEmail).ifPresent(u -> {
                throw new DuplicateResourceException("email");
            });
            currentUser.setEmail(updatedUserEmail);
        }

        // Update full name if provided
        if (updatedUser.getFullName() != null && !updatedUser.getFullName().isEmpty()) {
            currentUser.setFullName(updatedUser.getFullName());
        }

<<<<<<< Updated upstream
        if (updatedUser.getPhoneNumber() != null && !updatedUser.getPhoneNumber().isEmpty()) {
            currentUser.setPhoneNumber(PhoneNumberValidationUtil.normalizePhoneNumber(updatedUser.getPhoneNumber()));
        }

=======
        // Update password if provided (re-encoded for security)
>>>>>>> Stashed changes
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(currentUser);
    }
}