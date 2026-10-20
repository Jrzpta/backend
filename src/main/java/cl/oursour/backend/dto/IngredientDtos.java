package cl.oursour.backend.dto;

import cl.oursour.backend.model.IngredientCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
 
import java.util.List;
 
public final class IngredientDtos {
 
    private IngredientDtos() {}
 
    public record IngredientResponse(Long id, String name, IngredientCategory category) {}
 
    public record IngredientCreateRequest(
            @NotBlank(message = "El nombre es obligatorio") String name,
            @NotNull(message = "La categoría es obligatoria") IngredientCategory category) {}
 
    /** Para guardar la lista de ingredientes frecuentes (puede quedar vacía). */
    public record IngredientIdsRequest(
            @NotNull(message = "ingredientIds es obligatorio") List<Long> ingredientIds) {}
 
    /** Para buscar recetas (al menos un ingrediente). */
    public record RecipeSearchRequest(
            @NotEmpty(message = "Debes indicar al menos un ingrediente") List<Long> ingredientIds) {}
}

