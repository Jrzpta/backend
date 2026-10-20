package cl.oursour.backend.model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "products")
public class Product {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String name;
 
    @Column(nullable = false, length = 500)
    private String description;
 
    @Column(nullable = false)
    private int price;   // CLP, sin decimales
 
    @Column(nullable = false)
    private int stock;   // stock simulado
 
    @Column(nullable = false)
    private boolean active = true;
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

