package cl.oursour.backend.controller;

import cl.oursour.backend.dto.IngredientDtos.RecipeSearchRequest;
import cl.oursour.backend.dto.RecipeDtos.RecipeDetailResponse;
import cl.oursour.backend.dto.RecipeDtos.RecipeSearchResponse;
import cl.oursour.backend.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
 
    private final RecipeService recipeService;
 
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
 
    /** Motor de sugerencia: recibe ingredientes y devuelve recetas con % de compatibilidad. */
    @PostMapping("/search")
    public RecipeSearchResponse search(Authentication authentication,
                                       @Valid @RequestBody RecipeSearchRequest request) {
        return recipeService.search(authentication.getName(), request.ingredientIds());
    }
 
    /** Detalle de receta. Opcional: ?available=1,2,3 para calcular ingredientes faltantes. */
    @GetMapping("/{id}")
    public RecipeDetailResponse detail(Authentication authentication,
                                       @PathVariable Long id,
                                       @RequestParam(required = false) List<Long> available) {
        return recipeService.detail(authentication.getName(), id, available);
    }
}

