package cl.oursour.backend.controller;

import cl.oursour.backend.dto.ProductDtos.ProductResponse;
import cl.oursour.backend.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/products")
public class ProductController {
 
    private final ProductService productService;
 
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
 
    @GetMapping
    public List<ProductResponse> list() {
        return productService.listActive();
    }
 
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return productService.get(id);
    }
}

