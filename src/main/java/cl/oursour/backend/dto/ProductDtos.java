package cl.oursour.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
 
public final class ProductDtos {
 
    private ProductDtos() {}
 
    public record ProductResponse(Long id, String name, String description,
                                  int price, int stock, boolean active) {}
 
    public record ProductRequest(
            @NotBlank(message = "El nombre es obligatorio") String name,
            @NotBlank(message = "La descripción es obligatoria") String description,
            @NotNull(message = "El precio es obligatorio")
            @Min(value = 1, message = "El precio debe ser mayor a 0") Integer price,
            @NotNull(message = "El stock es obligatorio")
            @Min(value = 0, message = "El stock no puede ser negativo") Integer stock,
            Boolean active) {}
}
