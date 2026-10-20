package cl.oursour.backend.repository;

import cl.oursour.backend.model.Ingredient;
import cl.oursour.backend.model.IngredientCategory;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
 
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
 
    List<Ingredient> findAllByOrderByCategoryAscNameAsc();
 
    List<Ingredient> findByCategoryOrderByNameAsc(IngredientCategory category);
 
    boolean existsByNameIgnoreCase(String name);
}

