package cl.oursour.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
 
public final class AuthDtos {
 
    private AuthDtos() {}
 
    public record RegisterRequest(
            @NotBlank(message = "El nombre es obligatorio") @Size(max = 80) String name,
            @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") String email,
            @NotBlank(message = "La contraseña es obligatoria")
            @Size(min = 6, max = 60, message = "La contraseña debe tener entre 6 y 60 caracteres") String password) {}
 
    public record LoginRequest(
            @NotBlank(message = "El correo es obligatorio") @Email(message = "Correo inválido") String email,
            @NotBlank(message = "La contraseña es obligatoria") String password) {}
 
    public record AuthResponse(String token, String tokenType, Long userId,
                               String name, String email, String role) {}
}

