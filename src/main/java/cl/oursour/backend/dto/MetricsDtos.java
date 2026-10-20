package cl.oursour.backend.dto;

import java.util.List;
 
public final class MetricsDtos {
 
    private MetricsDtos() {}
 
    public record MetricItem(String label, long count) {}
 
    public record MetricsResponse(int days, long totalSearches,
                                  List<MetricItem> topIngredients,
                                  List<MetricItem> topRecipes,
                                  List<MetricItem> topCombinations,
                                  String message) {}
}

