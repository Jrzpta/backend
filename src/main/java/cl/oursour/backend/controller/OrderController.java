package cl.oursour.backend.controller;

import cl.oursour.backend.dto.OrderDtos.OrderResponse;
import cl.oursour.backend.dto.OrderDtos.PaymentRequest;
import cl.oursour.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/orders")
public class OrderController {
 
    private final OrderService orderService;
 
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
 
    @GetMapping
    public List<OrderResponse> myOrders(Authentication authentication) {
        return orderService.myOrders(authentication.getName());
    }
 
    @GetMapping("/{id}")
    public OrderResponse get(Authentication authentication, @PathVariable Long id) {
        return orderService.getOrder(authentication.getName(), id);
    }
 
    /** Pago sandbox: tarjeta terminada en 0002 = rechazado (HTTP 402). */
    @PostMapping("/{id}/pay")
    public OrderResponse pay(Authentication authentication, @PathVariable Long id,
                             @Valid @RequestBody PaymentRequest request) {
        return orderService.pay(authentication.getName(), id, request);
    }
}

