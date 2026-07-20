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

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest requestedUser) {
        requestedUser.setPhoneNumber(PhoneNumberValidationUtil.normalizePhoneNumber(requestedUser.getPhoneNumber()));

        if (userRepository.existsByUsername(requestedUser.getUsername())) {
            throw new DuplicateResourceException("username");
        }
        if (userRepository.existsByPhoneNumber(requestedUser.getPhoneNumber())) {
            throw new DuplicateResourceException("phone number");
        }
        if (userRepository.existsByEmail(requestedUser.getEmail())) {
            throw new DuplicateResourceException("email");
        }

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

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if(user.isBlocked()){
            throw new IllegalArgumentException("You have been blocked by admin");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return user;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UserIsNotAuthenticatedException();
        }

        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(UserIsNotAuthenticatedException::new);
    }

    public User updateUserProfile(UpdateUserRequest updatedUser) {
        User currentUser = getCurrentUser();

        String updatedUserEmail = updatedUser.getEmail();
        if (updatedUserEmail != null && !updatedUserEmail.isEmpty() && !currentUser.getEmail().equals(updatedUserEmail)) {
            userRepository.findByEmail(updatedUserEmail).ifPresent(u -> {
                throw new DuplicateResourceException("email");
            });
            currentUser.setEmail(updatedUserEmail);
        }

        if (updatedUser.getFullName() != null && !updatedUser.getFullName().isEmpty()) {
            currentUser.setFullName(updatedUser.getFullName());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(currentUser);
    }
}
