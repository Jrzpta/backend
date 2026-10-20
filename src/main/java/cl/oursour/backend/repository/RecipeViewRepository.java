package cl.oursour.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import cl.oursour.backend.model.RecipeView;
 
public interface RecipeViewRepository extends JpaRepository<RecipeView, Long> {
 
    @Query("select r.title, count(v) from RecipeView v join v.recipe r "
            + "where v.createdAt >= :since group by r.title order by count(v) desc")
    List<Object[]> topRecipes(@Param("since") LocalDateTime since, Pageable pageable);
}

