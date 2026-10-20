package cl.oursour.backend.repository;

import cl.oursour.backend.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.util.List;
import java.util.Optional;
 
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
 
    @Query("select distinct r from Recipe r "
            + "left join fetch r.ingredients ri "
            + "left join fetch ri.ingredient "
            + "left join fetch r.product")
    List<Recipe> findAllWithDetails();
 
    @Query("select distinct r from Recipe r "
            + "left join fetch r.ingredients ri "
            + "left join fetch ri.ingredient "
            + "left join fetch r.product "
            + "where r.id = :id")
    Optional<Recipe> findByIdWithDetails(@Param("id") Long id);
}

