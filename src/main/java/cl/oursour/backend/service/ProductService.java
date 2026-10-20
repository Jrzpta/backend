package cl.oursour.backend.service;

import cl.oursour.backend.dto.ProductDtos.ProductRequest;
import cl.oursour.backend.dto.ProductDtos.ProductResponse;
import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.Product;
import cl.oursour.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
 
@Service
public class ProductService {
 
    private final ProductRepository productRepository;
 
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
 
    @Transactional(readOnly = true)
    public List<ProductResponse> listActive() {
        return productRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(ProductService::toResponse).toList();
    }
 
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        return toResponse(find(id));
    }
 
    // ---------- Administrador (CU-05) ----------
 
    @Transactional(readOnly = true)
    public List<ProductResponse> listAll() {
        return productRepository.findAllByOrderByNameAsc().stream()
                .map(ProductService::toResponse).toList();
    }
 
    @Transactional
    public ProductResponse create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return toResponse(productRepository.save(p));
    }
 
    @Transactional
    public ProductResponse update(Long id, ProductRequest req) {
        Product p = find(id);
        apply(p, req);
        return toResponse(productRepository.save(p));
    }
 
    /** "Desactivar" producto: no se borra, solo deja de mostrarse. */
    @Transactional
    public void deactivate(Long id) {
        Product p = find(id);
        p.setActive(false);
        productRepository.save(p);
    }
 
    private Product find(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }
 
    private void apply(Product p, ProductRequest req) {
        p.setName(req.name().trim());
        p.setDescription(req.description().trim());
        p.setPrice(req.price());
        p.setStock(req.stock());
        p.setActive(req.active() == null || req.active());
    }
 
    public static ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getStock(), p.isActive());
    }
}

