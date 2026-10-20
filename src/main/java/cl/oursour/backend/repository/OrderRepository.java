package cl.oursour.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.backend.model.CustomerOrder;
import cl.oursour.backend.model.OrderStatus;
import cl.oursour.backend.model.User;
 
public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
 
    Optional<CustomerOrder> findFirstByUserAndStatus(User user, OrderStatus status);
 
    Optional<CustomerOrder> findByIdAndUser(Long id, User user);
 
    List<CustomerOrder> findByUserAndStatusNotOrderByCreatedAtDesc(User user, OrderStatus status);
}

