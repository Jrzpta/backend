package cl.oursour.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "recipes")
public class Recipe {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 120)
    private String title;
 
    @Column(length = 500)
    private String description;
 
    // Pasos separados por salto de linea
    @Column(nullable = false, columnDefinition = "TEXT")
    private String steps;
 
    // Producto Our Sour asociado a la receta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
 
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}

