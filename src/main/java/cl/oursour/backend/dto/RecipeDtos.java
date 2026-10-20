package cl.oursour.backend.dto;

import cl.oursour.backend.dto.ProductDtos.ProductResponse;
import cl.oursour.backend.model.IngredientCategory;
 
import java.util.List;
 
public final class RecipeDtos {
 
    private RecipeDtos() {}
 
    public record RecipeMatchResponse(Long id, String title, String description,
                                      int matchPercentage, List<String> missingIngredients,
                                      boolean needsOurSourProduct, ProductResponse product) {}
 
    public record RecipeSearchResponse(List<RecipeMatchResponse> recipes,
                                       List<ProductResponse> suggestedProducts,
                                       String message) {}
 
    public record RecipeIngredientResponse(Long ingredientId, String name,
                                           IngredientCategory category,
                                           String quantity, boolean available) {}
 
    public record RecipeDetailResponse(Long id, String title, String description,
                                       List<RecipeIngredientResponse> ingredients,
                                       List<String> steps, int matchPercentage,
                                       List<String> missingIngredients,
                                       boolean needsOurSourProduct, ProductResponse product) {}
}

