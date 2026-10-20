package cl.oursour.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Representa el Carro/Pedido del anexo.
 * Mientras esta EN_PREPARACION es el carro; luego pasa a SOLICITADO y PAGADO.
 */
@Entity
@Table(name = "orders")
public class CustomerOrder {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.EN_PREPARACION;
 
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
 
    private int subtotal;
    private int shippingCost;
    private int total;
 
    @Column(length = 150)
    private String street;
 
    @Column(length = 80)
    private String city;
 
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
 
    private LocalDateTime paidAt;
 
    @Column(length = 40)
    private String paymentReference;
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getShippingCost() { return shippingCost; }
    public void setShippingCost(int shippingCost) { this.shippingCost = shippingCost; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public String getPaymentReference() { return paymentReference; }
    public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }
}

