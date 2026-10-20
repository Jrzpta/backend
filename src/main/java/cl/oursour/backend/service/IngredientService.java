package cl.oursour.backend.service;

import cl.oursour.backend.dto.IngredientDtos.IngredientCreateRequest;
import cl.oursour.backend.dto.IngredientDtos.IngredientResponse;
import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.Ingredient;
import cl.oursour.backend.model.IngredientCategory;
import cl.oursour.backend.model.User;
import cl.oursour.backend.repository.IngredientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
 
@Service
public class IngredientService {
 
    private final IngredientRepository ingredientRepository;
    private final UserService userService;
 
    public IngredientService(IngredientRepository ingredientRepository, UserService userService) {
        this.ingredientRepository = ingredientRepository;
        this.userService = userService;
    }
 
    @Transactional(readOnly = true)
    public List<IngredientResponse> list(IngredientCategory category) {
        List<Ingredient> list = (category == null)
                ? ingredientRepository.findAllByOrderByCategoryAscNameAsc()
                : ingredientRepository.findByCategoryOrderByNameAsc(category);
        return list.stream().map(IngredientService::toResponse).toList();
    }
 
    /** Flujo alternativo de CU-01: agregar un ingrediente manualmente. */
    @Transactional
    public IngredientResponse create(IngredientCreateRequest req) {
        String name = req.name().trim();
        if (ingredientRepository.existsByNameIgnoreCase(name)) {
            throw new ApiException(HttpStatus.CONFLICT, "El ingrediente ya existe");
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setCategory(req.category());
        return toResponse(ingredientRepository.save(ingredient));
    }
 
    @Transactional(readOnly = true)
    public List<IngredientResponse> myIngredients(String email) {
        User user = userService.getByEmail(email);
        return user.getFrequentIngredients().stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .map(IngredientService::toResponse)
                .toList();
    }
 
    /** CU-01: guarda (reemplaza) la lista de ingredientes frecuentes del usuario. */
    @Transactional
    public List<IngredientResponse> saveMyIngredients(String email, List<Long> ids) {
        User user = userService.getByEmail(email);
        List<Ingredient> found = resolveIngredients(ids);
        user.getFrequentIngredients().clear();
        user.getFrequentIngredients().addAll(found);
        return found.stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .map(IngredientService::toResponse)
                .toList();
    }
 
    /** Valida que todos los ids existan en el catálogo (consistencia de ingredientes). */
    @Transactional(readOnly = true)
    public List<Ingredient> resolveIngredients(List<Long> ids) {
        Set<Long> distinct = new LinkedHashSet<>(ids);
        List<Ingredient> found = ingredientRepository.findAllById(distinct);
        if (found.size() != distinct.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Algunos ingredientes enviados no existen en el catálogo");
        }
        return found;
    }
 
    public static IngredientResponse toResponse(Ingredient i) {
        return new IngredientResponse(i.getId(), i.getName(), i.getCategory());
    }
}

