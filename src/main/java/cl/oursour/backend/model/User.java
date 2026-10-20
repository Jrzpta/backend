package cl.oursour.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
 
@Entity
@Table(name = "users")
public class User {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 80)
    private String name;
 
    @Column(nullable = false, unique = true, length = 120)
    private String email;
 
    @Column(nullable = false)
    private String password; // hash BCrypt
 
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
 
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
 
    // Ingredientes frecuentes del usuario (CU-01)
    @ManyToMany
    @JoinTable(name = "user_ingredients",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> frequentIngredients = new HashSet<>();
 
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Ingredient> getFrequentIngredients() { return frequentIngredients; }
    public void setFrequentIngredients(Set<Ingredient> frequentIngredients) { this.frequentIngredients = frequentIngredients; }
}

