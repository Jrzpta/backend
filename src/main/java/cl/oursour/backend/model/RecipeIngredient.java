package cl.oursour.backend.model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
 
    @Column(nullable = false, length = 50)
    private String quantity; // ej: "60 ml", "2 rodajas", "Al gusto"
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public Ingredient getIngredient() { return ingredient; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
}

