package cl.oursour.backend.controller;

import cl.oursour.backend.dto.ProductDtos.ProductRequest;
import cl.oursour.backend.dto.ProductDtos.ProductResponse;
import cl.oursour.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
/** CU-05. Solo accesible con rol ADMIN (ver SecurityConfig). */
@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {
 
    private final ProductService productService;
 
    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }
 
    @GetMapping
    public List<ProductResponse> listAll() {
        return productService.listAll();
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }
 
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        productService.deactivate(id);
    }
}

