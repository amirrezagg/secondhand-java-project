package ir.aut.secondhand.service;

import ir.aut.secondhand.dto.RegisterRequest;
import ir.aut.secondhand.dto.UpdateUserRequest;
import ir.aut.secondhand.exception.DuplicateResourceException;
import ir.aut.secondhand.exception.UserIsNotAuthenticatedException;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository stubUserRepository;
    private PasswordEncoder stubPasswordEncoder;

    private User mockUser;

    private User lastSavedUser;
    private boolean existsByUsernameResult = false;
    private User userToReturn;

    @BeforeEach
    void setUp() {

        stubPasswordEncoder = new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return "encoded_" + rawPassword;
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals("encoded_" + rawPassword);
            }
        };

        stubUserRepository = (UserRepository) java.lang.reflect.Proxy.newProxyInstance(
                UserRepository.class.getClassLoader(),
                new Class<?>[]{UserRepository.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "existsByUsername":
                            return existsByUsernameResult;
                        case "existsByPhoneNumber":
                        case "existsByEmail":
                            return false;
                        case "save":
                            lastSavedUser = (User) args[0];
                            return lastSavedUser;
                        case "findByUsername":
                            return Optional.ofNullable(userToReturn);
                        case "findByEmail":
                            return Optional.empty();
                        case "findByUsernameAndIsBlocked":
                            if (userToReturn != null && args[1] != null && userToReturn.isBlocked() == (Boolean) args[1]) {
                                return Optional.of(userToReturn);
                            }
                            return Optional.empty();
                        default:
                            return null;
                    }
                });

        userService = new UserService(stubUserRepository, stubPasswordEncoder);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("amir");
        mockUser.setPassword("encoded_correctPassword");
        mockUser.setFullName("Amir");
        mockUser.setPhoneNumber("+989123456789");
        mockUser.setEmail("amir@example.com");
        mockUser.setRole(User.Role.USER);
        mockUser.setBlocked(false);

        lastSavedUser = null;
        existsByUsernameResult = false;
        userToReturn = null;
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("amir");
        request.setPassword("plainPassword");
        request.setFullName("Amir");
        request.setPhoneNumber("09123456789");
        request.setEmail("amir@example.com");

        User result = userService.register(request);

        assertNotNull(result);
        assertEquals("amir", result.getUsername());
        assertEquals("encoded_plainPassword", result.getPassword());
        assertNotNull(lastSavedUser);
    }

    @Test
    void testRegister_DuplicateUsername_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("duplicateUser");
        request.setPhoneNumber("09123456789");
        request.setPassword("password");
        request.setEmail("test@example.com");

        existsByUsernameResult = true;

        assertThrows(DuplicateResourceException.class, () -> userService.register(request));
    }

    @Test
    void testLogin_Success() {
        userToReturn = mockUser;

        User result = userService.login("amir", "correctPassword");

        assertNotNull(result);
        assertEquals("amir", result.getUsername());
    }

    @Test
    void testLogin_BlockedUser_ThrowsException() {
        mockUser.setBlocked(true);
        userToReturn = mockUser;

        assertThrows(IllegalArgumentException.class, () -> userService.login("amir", "correctPassword"));
    }

    @Test
    void testLogin_WrongPassword_ThrowsException() {
        userToReturn = mockUser;

        assertThrows(BadCredentialsException.class, () -> userService.login("amir", "wrongPassword"));
    }

    @Test
    void testUpdateUserProfile_Success() {
        org.springframework.security.authentication.UsernamePasswordAuthenticationToken realAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "amir", "encoded_correctPassword", java.util.Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(realAuth);
        userToReturn = mockUser;

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setFullName("Amir Updated");
        updateRequest.setPhoneNumber("09998887766");
        updateRequest.setPassword("newPassword");

        User result = userService.updateUserProfile(updateRequest);

        assertNotNull(result);
        assertEquals("Amir Updated", result.getFullName());
        assertEquals("encoded_newPassword", result.getPassword());
    }

    @Test
    void testGetCurrentUser_NotAuthenticated_ThrowsException() {
        SecurityContextHolder.clearContext();

        assertThrows(UserIsNotAuthenticatedException.class, () -> userService.getCurrentUser());
    }
}