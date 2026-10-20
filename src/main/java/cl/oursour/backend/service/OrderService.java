package cl.oursour.backend.service;

import cl.oursour.backend.dto.OrderDtos.*;
import cl.oursour.backend.exception.ApiException;
import cl.oursour.backend.model.*;
import cl.oursour.backend.repository.OrderRepository;
import cl.oursour.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
 
@Service
public class OrderService {
 
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final int freeShippingThreshold;
    private final int shippingFlatCost;
 
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserService userService,
                        @Value("${app.shipping.free-threshold}") int freeShippingThreshold,
                        @Value("${app.shipping.cost}") int shippingFlatCost) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userService = userService;
        this.freeShippingThreshold = freeShippingThreshold;
        this.shippingFlatCost = shippingFlatCost;
    }
 
    // ------------------------- CARRO (CU-04) -------------------------
 
    @Transactional
    public OrderResponse getCart(String email) {
        return toResponse(getOrCreateCart(userService.getByEmail(email)));
    }
 
    @Transactional
    public OrderResponse addItem(String email, CartItemRequest req) {
        CustomerOrder cart = getOrCreateCart(userService.getByEmail(email));
        Product product = productRepository.findById(req.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no disponible"));
 
        OrderItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);
 
        int newQuantity = (item == null ? 0 : item.getQuantity()) + req.quantity();
        checkStock(product, newQuantity);
 
        if (item == null) {
            item = new OrderItem();
            item.setOrder(cart);
            item.setProduct(product);
            item.setUnitPrice(product.getPrice());
            cart.getItems().add(item);
        }
        item.setQuantity(newQuantity);
 
        recalculate(cart);
        return toResponse(orderRepository.saveAndFlush(cart));
    }
 
    @Transactional
    public OrderResponse updateQuantity(String email, Long itemId, int quantity) {
        CustomerOrder cart = getOrCreateCart(userService.getByEmail(email));
        OrderItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no está en el carro"));
        checkStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        recalculate(cart);
        return toResponse(orderRepository.saveAndFlush(cart));
    }
 
    @Transactional
    public OrderResponse removeItem(String email, Long itemId) {
        CustomerOrder cart = getOrCreateCart(userService.getByEmail(email));
        boolean removed = cart.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El producto no está en el carro");
        }
        recalculate(cart);
        return toResponse(orderRepository.saveAndFlush(cart));
    }
 
    // ---------------- DESPACHO + CHECKOUT ----------------
 
    @Transactional
    public OrderResponse checkout(String email, CheckoutRequest req) {
        CustomerOrder cart = getOrCreateCart(userService.getByEmail(email));
        if (cart.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El carro está vacío");
        }
        for (OrderItem item : cart.getItems()) {
            checkStock(item.getProduct(), item.getQuantity());
        }
        cart.setStreet(req.street().trim());
        cart.setCity(req.city().trim());
        recalculate(cart);
        cart.setStatus(OrderStatus.SOLICITADO);
        return toResponse(orderRepository.saveAndFlush(cart));
    }
 
    // ---------------- PAGO SANDBOX ----------------
 
    /**
     * Pago simulado. Regla: las tarjetas que terminan en 0002 son rechazadas;
     * cualquier otra es aprobada. No se guarda ningún dato de tarjeta.
     */
    @Transactional
    public OrderResponse pay(String email, Long orderId, PaymentRequest req) {
        User user = userService.getByEmail(email);
        CustomerOrder order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
 
        if (order.getStatus() != OrderStatus.SOLICITADO) {
            throw new ApiException(HttpStatus.CONFLICT, "El pedido no está pendiente de pago");
        }
        if (req.cardNumber().endsWith("0002")) {
            throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "Pago rechazado por el procesador sandbox");
        }
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            checkStock(p, item.getQuantity());
            p.setStock(p.getStock() - item.getQuantity());
        }
        order.setStatus(OrderStatus.PAGADO);
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentReference("SBX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return toResponse(orderRepository.saveAndFlush(order));
    }
 
    // ---------------- MIS PEDIDOS ----------------
 
    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders(String email) {
        User user = userService.getByEmail(email);
        return orderRepository.findByUserAndStatusNotOrderByCreatedAtDesc(user, OrderStatus.EN_PREPARACION)
                .stream().map(this::toResponse).toList();
    }
 
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String email, Long id) {
        User user = userService.getByEmail(email);
        return orderRepository.findByIdAndUser(id, user)
                .map(this::toResponse)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido no encontrado"));
    }
 
    // ---------------- Auxiliares ----------------
 
    private CustomerOrder getOrCreateCart(User user) {
        return orderRepository.findFirstByUserAndStatus(user, OrderStatus.EN_PREPARACION)
                .orElseGet(() -> {
                    CustomerOrder cart = new CustomerOrder();
                    cart.setUser(user);
                    cart.setStatus(OrderStatus.EN_PREPARACION);
                    return orderRepository.save(cart);
                });
    }
 
    private void checkStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Stock insuficiente para " + product.getName() + " (disponible: " + product.getStock() + ")");
        }
    }
 
    /** Subtotal + despacho (gratis sobre el umbral) + total. */
    private void recalculate(CustomerOrder order) {
        int subtotal = order.getItems().stream()
                .mapToInt(i -> i.getUnitPrice() * i.getQuantity()).sum();
        int shipping = (subtotal == 0 || subtotal >= freeShippingThreshold) ? 0 : shippingFlatCost;
        order.setSubtotal(subtotal);
        order.setShippingCost(shipping);
        order.setTotal(subtotal + shipping);
    }
 
    private OrderResponse toResponse(CustomerOrder o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(i.getId(), i.getProduct().getId(), i.getProduct().getName(),
                        i.getUnitPrice(), i.getQuantity(), i.getUnitPrice() * i.getQuantity()))
                .toList();
        return new OrderResponse(o.getId(), o.getStatus().name(), items, o.getSubtotal(),
                o.getShippingCost(), o.getTotal(), o.getStreet(), o.getCity(),
                o.getCreatedAt(), o.getPaymentReference());
    }
}

