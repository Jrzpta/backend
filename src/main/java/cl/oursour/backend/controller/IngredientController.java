package cl.oursour.backend.controller;

import cl.oursour.backend.dto.IngredientDtos.IngredientCreateRequest;
import cl.oursour.backend.dto.IngredientDtos.IngredientIdsRequest;
import cl.oursour.backend.dto.IngredientDtos.IngredientResponse;
import cl.oursour.backend.model.IngredientCategory;
import cl.oursour.backend.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api")
public class IngredientController {
 
    private final IngredientService ingredientService;
 
    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }
 
    /** Catálogo de ingredientes. Filtro opcional: ?category=DESTILADO */
    @GetMapping("/ingredients")
    public List<IngredientResponse> list(@RequestParam(required = false) IngredientCategory category) {
        return ingredientService.list(category);
    }
 
    /** Agregar un ingrediente manualmente (flujo alternativo de CU-01). */
    @PostMapping("/ingredients")
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientResponse create(@Valid @RequestBody IngredientCreateRequest request) {
        return ingredientService.create(request);
    }
 
    /** Ingredientes frecuentes del usuario autenticado. */
    @GetMapping("/me/ingredients")
    public List<IngredientResponse> myIngredients(Authentication authentication) {
        return ingredientService.myIngredients(authentication.getName());
    }
 
    /** Guarda/reemplaza los ingredientes frecuentes del usuario. */
    @PutMapping("/me/ingredients")
    public List<IngredientResponse> saveMyIngredients(Authentication authentication,
                                                      @Valid @RequestBody IngredientIdsRequest request) {
        return ingredientService.saveMyIngredients(authentication.getName(), request.ingredientIds());
    }
}
