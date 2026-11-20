package vn.edu.nguyentrai.rooms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.edu.nguyentrai.rooms.dto.LoginRequest;
import vn.edu.nguyentrai.rooms.dto.LoginResponse;
import vn.edu.nguyentrai.rooms.entity.User;
import vn.edu.nguyentrai.rooms.repository.UserRepository;
import vn.edu.nguyentrai.rooms.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (user == null || !user.isActive()
                || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Sai tên đăng nhập hoặc mật khẩu");
        }

        String role = user.getRole().getName();
        String token = jwtUtil.generateToken(user.getUsername(), role);
        return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getFullName(), role));
    }
}