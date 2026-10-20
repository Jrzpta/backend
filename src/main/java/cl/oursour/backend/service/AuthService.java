package cl.oursour.backend.service;

import cl.oursour.backend.dto.AuthDtos.AuthResponse;
import cl.oursour.backend.dto.AuthDtos.LoginRequest;
import cl.oursour.backend.dto.AuthDtos.RegisterRequest;
import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.Role;
import cl.oursour.backend.model.User;
import cl.oursour.backend.repository.UserRepository;
import cl.oursour.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
 
@Service
public class AuthService {
 
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
 
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
 
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya está registrado");
        }
        User user = new User();
        user.setName(req.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.USER); // el registro público siempre crea usuarios normales
        userRepository.save(user);
        return toResponse(user);
    }
 
    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        return toResponse(user);
    }
 
    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), "Bearer", user.getId(),
                user.getName(), user.getEmail(), user.getRole().name());
    }
}

