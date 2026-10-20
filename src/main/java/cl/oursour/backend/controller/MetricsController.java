package cl.oursour.backend.controller;

import cl.oursour.backend.dto.MetricsDtos.MetricsResponse;
import cl.oursour.backend.service.MetricsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
 
/** CU-06. Solo accesible con rol ADMIN. */
@RestController
@RequestMapping("/api/admin/metrics")
public class MetricsController {
 
    private final MetricsService metricsService;
 
    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }
 
    @GetMapping
    public MetricsResponse metrics(@RequestParam(defaultValue = "30") int days) {
        int safeDays = Math.max(1, Math.min(days, 365));
        return metricsService.getMetrics(safeDays);
    }
}

