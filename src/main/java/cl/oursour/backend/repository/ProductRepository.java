package cl.oursour.backend.repository;

import cl.oursour.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
 
public interface ProductRepository extends JpaRepository<Product, Long> {
 
    List<Product> findByActiveTrueOrderByNameAsc();
 
    List<Product> findAllByOrderByNameAsc();
}