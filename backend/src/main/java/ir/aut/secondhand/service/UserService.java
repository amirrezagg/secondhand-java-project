package ir.aut.secondhand.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ir.aut.secondhand.dto.RegisterRequest;
import ir.aut.secondhand.exception.BadCredentialsException;
import ir.aut.secondhand.exception.EmailAlreadyExistsException;
import ir.aut.secondhand.exception.PhoneNumberAlreadyExistsException;
import ir.aut.secondhand.exception.UsernameAlreadyExistsException;
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
            throw new UsernameAlreadyExistsException();
        }
        if (userRepository.existsByPhoneNumber(requestedUser.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException();
        }
        if (userRepository.existsByEmail(requestedUser.getEmail())) {
            throw new EmailAlreadyExistsException();
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
        User user = userRepository.findByUsernameAndIsBlocked(username, false)
                .orElseThrow(() -> new BadCredentialsException());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException();
        }

        return user;
    }
}
