package cl.oursour.backend.repository;

import cl.oursour.backend.model.SearchLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import java.time.LocalDateTime;
import java.util.List;
 
public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
 
    long countByCreatedAtAfter(LocalDateTime since);
 
    @Query("select i.name, count(s) from SearchLog s join s.ingredients i "
            + "where s.createdAt >= :since group by i.name order by count(s) desc")
    List<Object[]> topIngredients(@Param("since") LocalDateTime since, Pageable pageable);
 
    @Query("select distinct s from SearchLog s left join fetch s.ingredients "
            + "where s.createdAt >= :since")
    List<SearchLog> findSinceWithIngredients(@Param("since") LocalDateTime since);
}

