package cl.oursour.backend.service;

import cl.oursour.backend.dto.MetricsDtos.MetricItem;
import cl.oursour.backend.dto.MetricsDtos.MetricsResponse;
import cl.oursour.backend.model.Ingredient;
import cl.oursour.backend.model.SearchLog;
import cl.oursour.backend.repository.RecipeViewRepository;
import cl.oursour.backend.repository.SearchLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
 
@Service
public class MetricsService {
 
    private final SearchLogRepository searchLogRepository;
    private final RecipeViewRepository recipeViewRepository;
 
    public MetricsService(SearchLogRepository searchLogRepository, RecipeViewRepository recipeViewRepository) {
        this.searchLogRepository = searchLogRepository;
        this.recipeViewRepository = recipeViewRepository;
    }
 
    /** CU-06: datos agregados de los últimos N días. */
    @Transactional(readOnly = true)
    public MetricsResponse getMetrics(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable top5 = PageRequest.of(0, 5);
 
        long totalSearches = searchLogRepository.countByCreatedAtAfter(since);
 
        List<MetricItem> topIngredients = searchLogRepository.topIngredients(since, top5).stream()
                .map(r -> new MetricItem((String) r[0], (Long) r[1])).toList();
 
        List<MetricItem> topRecipes = recipeViewRepository.topRecipes(since, top5).stream()
                .map(r -> new MetricItem((String) r[0], (Long) r[1])).toList();
 
        List<MetricItem> topCombinations = computeTopPairs(since);
 
        String message = (totalSearches == 0)
                ? "No hay datos suficientes en el período consultado." : null;
 
        return new MetricsResponse(days, totalSearches, topIngredients, topRecipes, topCombinations, message);
    }
 
    /** Pares de ingredientes que aparecen juntos con más frecuencia en las búsquedas. */
    private List<MetricItem> computeTopPairs(LocalDateTime since) {
        Map<String, Long> counts = new HashMap<>();
        for (SearchLog log : searchLogRepository.findSinceWithIngredients(since)) {
            List<String> names = log.getIngredients().stream()
                    .map(Ingredient::getName).sorted().toList();
            for (int i = 0; i < names.size(); i++) {
                for (int j = i + 1; j < names.size(); j++) {
                    counts.merge(names.get(i) + " + " + names.get(j), 1L, Long::sum);
                }
            }
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(5)
                .map(e -> new MetricItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}

