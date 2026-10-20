package cl.oursour.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
 
/** Registro de cada busqueda de recetas (alimenta las metricas, CU-06). */
@Entity
@Table(name = "search_logs")
public class SearchLog {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
 
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
 
    @ManyToMany
    @JoinTable(name = "search_log_ingredients",
            joinColumns = @JoinColumn(name = "search_log_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients = new HashSet<>();
 
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(Set<Ingredient> ingredients) { this.ingredients = ingredients; }
}

