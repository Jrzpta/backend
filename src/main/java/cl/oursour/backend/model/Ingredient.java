package cl.oursour.backend.model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "ingredients")
public class Ingredient {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, unique = true, length = 80)
    private String name;
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IngredientCategory category;
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public IngredientCategory getCategory() { return category; }
    public void setCategory(IngredientCategory category) { this.category = category; }
}

