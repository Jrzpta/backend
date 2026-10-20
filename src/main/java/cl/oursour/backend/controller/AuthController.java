package cl.oursour.backend.controller;

import cl.oursour.backend.dto.AuthDtos.AuthResponse;
import cl.oursour.backend.dto.AuthDtos.LoginRequest;
import cl.oursour.backend.dto.AuthDtos.RegisterRequest;
import cl.oursour.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 
    private final AuthService authService;
 
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
 
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
 
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}


