package cl.oursour.backend.controller;

import cl.oursour.backend.dto.OrderDtos.*;
import cl.oursour.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/cart")
public class CartController {
 
    private final OrderService orderService;
 
    public CartController(OrderService orderService) {
        this.orderService = orderService;
    }
 
    @GetMapping
    public OrderResponse getCart(Authentication authentication) {
        return orderService.getCart(authentication.getName());
    }
 
    @PostMapping("/items")
    public OrderResponse addItem(Authentication authentication, @Valid @RequestBody CartItemRequest request) {
        return orderService.addItem(authentication.getName(), request);
    }
 
    @PutMapping("/items/{itemId}")
    public OrderResponse updateQuantity(Authentication authentication, @PathVariable Long itemId,
                                        @Valid @RequestBody UpdateQuantityRequest request) {
        return orderService.updateQuantity(authentication.getName(), itemId, request.quantity());
    }
 
    @DeleteMapping("/items/{itemId}")
    public OrderResponse removeItem(Authentication authentication, @PathVariable Long itemId) {
        return orderService.removeItem(authentication.getName(), itemId);
    }
 
    /** Dirección + cálculo de despacho; el carro pasa a SOLICITADO. */
    @PostMapping("/checkout")
    public OrderResponse checkout(Authentication authentication, @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(authentication.getName(), request);
    }
}
