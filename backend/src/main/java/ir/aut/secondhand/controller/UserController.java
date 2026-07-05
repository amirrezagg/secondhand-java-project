package ir.aut.secondhand.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ir.aut.secondhand.dto.LoginRequest;
import ir.aut.secondhand.dto.LoginResponse;
import ir.aut.secondhand.dto.RegisterRequest;
import ir.aut.secondhand.dto.RegisterResponse;
import ir.aut.secondhand.model.User;
import ir.aut.secondhand.service.UserService;
import ir.aut.secondhand.util.JwtUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest user) {
        User registeredUser = userService.register(user);
        return ResponseEntity.ok(new RegisterResponse(
                registeredUser.getId(),
                registeredUser.getUsername(),
                registeredUser.getRole()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getFullName(), user.getRole(), token));
    }
}
