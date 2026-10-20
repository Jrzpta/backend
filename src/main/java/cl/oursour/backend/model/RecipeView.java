package cl.oursour.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
 
/** Registro de cada vez que un usuario abre el detalle de una receta. */
@Entity
@Table(name = "recipe_views")
public class RecipeView {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
 
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
 
    public Long getId() { return id; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

