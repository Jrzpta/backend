package cl.oursour.backend.model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "order_items")
public class OrderItem {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id")
    private CustomerOrder order;
 
    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;
 
    @Column(nullable = false)
    private int quantity;
 
    @Column(nullable = false)
    private int unitPrice; // precio al momento de agregar al carro
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CustomerOrder getOrder() { return order; }
    public void setOrder(CustomerOrder order) { this.order = order; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getUnitPrice() { return unitPrice; }
    public void setUnitPrice(int unitPrice) { this.unitPrice = unitPrice; }
}

