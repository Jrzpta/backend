package cl.oursour.backend.service;

import cl.oursour.backend.dto.ProductDtos.ProductResponse;
import cl.oursour.backend.dto.RecipeDtos.RecipeDetailResponse;
import cl.oursour.backend.dto.RecipeDtos.RecipeIngredientResponse;
import cl.oursour.backend.dto.RecipeDtos.RecipeMatchResponse;
import cl.oursour.backend.dto.RecipeDtos.RecipeSearchResponse;
import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.*;
import cl.oursour.backend.repository.ProductRepository;
import cl.oursour.backend.repository.RecipeRepository;
import cl.oursour.backend.repository.RecipeViewRepository;
import cl.oursour.backend.repository.SearchLogRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.*;
import java.util.stream.Collectors;
 
@Service
public class RecipeService {
 
    private final RecipeRepository recipeRepository;
    private final SearchLogRepository searchLogRepository;
    private final RecipeViewRepository recipeViewRepository;
    private final ProductRepository productRepository;
    private final IngredientService ingredientService;
    private final UserService userService;
 
    public RecipeService(RecipeRepository recipeRepository,
                         SearchLogRepository searchLogRepository,
                         RecipeViewRepository recipeViewRepository,
                         ProductRepository productRepository,
                         IngredientService ingredientService,
                         UserService userService) {
        this.recipeRepository = recipeRepository;
        this.searchLogRepository = searchLogRepository;
        this.recipeViewRepository = recipeViewRepository;
        this.productRepository = productRepository;
        this.ingredientService = ingredientService;
        this.userService = userService;
    }
 
    /** Resultado interno del cálculo de compatibilidad. */
    private record Match(int percentage, List<String> missing, boolean needsOurSour) {}
 
    // ------------------------------------------------------------------
    // CU-02: motor de sugerencia de recetas
    // ------------------------------------------------------------------
    @Transactional
    public RecipeSearchResponse search(String email, List<Long> ingredientIds) {
        User user = userService.getByEmail(email);
        List<Ingredient> available = ingredientService.resolveIngredients(ingredientIds);
        Set<Long> availableIds = available.stream().map(Ingredient::getId).collect(Collectors.toSet());
 
        // Se registra la búsqueda para las métricas (CU-06)
        SearchLog log = new SearchLog();
        log.setUser(user);
        log.setIngredients(new HashSet<>(available));
        searchLogRepository.save(log);
 
        List<RecipeMatchResponse> matches = recipeRepository.findAllWithDetails().stream()
                .map(r -> toMatchResponse(r, evaluate(r, availableIds)))
                .filter(m -> m.matchPercentage() > 0)
                .sorted(Comparator.comparing(RecipeMatchResponse::matchPercentage, Comparator.reverseOrder())
                        .thenComparing(RecipeMatchResponse::title))
                .toList();
 
        if (matches.isEmpty()) {
            // Flujo alternativo: sin coincidencias -> sugerir productos Our Sour
            List<ProductResponse> suggestions = productRepository.findByActiveTrueOrderByNameAsc().stream()
                    .limit(3).map(ProductService::toResponse).toList();
            return new RecipeSearchResponse(List.of(), suggestions,
                    "No encontramos recetas compatibles. Te sugerimos estos productos Our Sour.");
        }
        return new RecipeSearchResponse(matches, List.of(), null);
    }
 
    // ------------------------------------------------------------------
    // CU-03: detalle de receta + producto asociado + faltantes
    // ------------------------------------------------------------------
    @Transactional
    public RecipeDetailResponse detail(String email, Long recipeId, List<Long> availableParam) {
        User user = userService.getByEmail(email);
        Recipe recipe = recipeRepository.findByIdWithDetails(recipeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Receta no encontrada"));
 
        // Si la app no envía ingredientes, se usan los ingredientes frecuentes guardados
        Set<Long> availableIds = (availableParam == null || availableParam.isEmpty())
                ? user.getFrequentIngredients().stream().map(Ingredient::getId).collect(Collectors.toSet())
                : new HashSet<>(availableParam);
 
        RecipeView view = new RecipeView();
        view.setRecipe(recipe);
        view.setUser(user);
        recipeViewRepository.save(view);
 
        Match match = evaluate(recipe, availableIds);
 
        List<RecipeIngredientResponse> ingredients = recipe.getIngredients().stream()
                .map(ri -> new RecipeIngredientResponse(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getIngredient().getCategory(),
                        ri.getQuantity(),
                        availableIds.contains(ri.getIngredient().getId())))
                .toList();
 
        List<String> steps = Arrays.stream(recipe.getSteps().split("\\r?\\n"))
                .filter(s -> !s.isBlank())
                .toList();
 
        return new RecipeDetailResponse(recipe.getId(), recipe.getTitle(), recipe.getDescription(),
                ingredients, steps, match.percentage(), match.missing(), match.needsOurSour(),
                recipe.getProduct() == null ? null : ProductService.toResponse(recipe.getProduct()));
    }
 
    // ------------------------------------------------------------------
    // Cálculo de compatibilidad: ingredientes disponibles / requeridos
    // ------------------------------------------------------------------
    private Match evaluate(Recipe recipe, Set<Long> availableIds) {
        int total = recipe.getIngredients().size();
        List<String> missing = new ArrayList<>();
        boolean needsOurSour = false;
 
        for (RecipeIngredient ri : recipe.getIngredients()) {
            Ingredient ing = ri.getIngredient();
            if (!availableIds.contains(ing.getId())) {
                missing.add(ing.getName());
                if (ing.getCategory() == IngredientCategory.PRODUCTO_OUR_SOUR) {
                    needsOurSour = true;
                }
            }
        }
        int matched = total - missing.size();
        int percentage = (total == 0) ? 0 : Math.round(matched * 100f / total);
        return new Match(percentage, missing, needsOurSour);
    }
 
    private RecipeMatchResponse toMatchResponse(Recipe r, Match m) {
        return new RecipeMatchResponse(r.getId(), r.getTitle(), r.getDescription(),
                m.percentage(), m.missing(), m.needsOurSour(),
                r.getProduct() == null ? null : ProductService.toResponse(r.getProduct()));
    }
}

