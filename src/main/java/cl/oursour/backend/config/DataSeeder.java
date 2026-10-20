package cl.oursour.backend.config;

import cl.oursour.backend.model.*;
import cl.oursour.backend.repository.IngredientRepository;
import cl.oursour.backend.repository.ProductRepository;
import cl.oursour.backend.repository.RecipeRepository;
import cl.oursour.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.HashMap;
import java.util.Map;
 
/**
 * Carga datos SINTETICOS la primera vez que se levanta el backend
 * (el anexo prohíbe usar datos reales ni fórmulas reales de Our Sour).
 */
@Component
public class DataSeeder implements CommandLineRunner {
 
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final RecipeRepository recipeRepository;
    private final PasswordEncoder passwordEncoder;
 
    private final Map<String, Ingredient> ingredients = new HashMap<>();
 
    public DataSeeder(UserRepository userRepository, IngredientRepository ingredientRepository,
                      ProductRepository productRepository, RecipeRepository recipeRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.recipeRepository = recipeRepository;
        this.passwordEncoder = passwordEncoder;
    }
 
    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // ya hay datos
        }
        seedUsers();
        seedIngredients();
        Map<String, Product> products = seedProducts();
        seedRecipes(products);
        System.out.println(">>> Datos de prueba cargados (admin@oursour.cl / Admin123!  |  demo@oursour.cl / Demo123!)");
    }
 
    private void seedUsers() {
        createUser("Admin Our Sour", "admin@oursour.cl", "Admin123!", Role.ADMIN);
        createUser("Usuario Demo", "demo@oursour.cl", "Demo123!", Role.USER);
    }
 
    private void createUser(String name, String email, String rawPassword, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(role);
        userRepository.save(u);
    }
 
    private void seedIngredients() {
        ing("Our Sour Limón", IngredientCategory.PRODUCTO_OUR_SOUR);
        ing("Our Sour Maracuyá", IngredientCategory.PRODUCTO_OUR_SOUR);
        ing("Our Sour Frutilla", IngredientCategory.PRODUCTO_OUR_SOUR);
 
        ing("Gin", IngredientCategory.DESTILADO);
        ing("Vodka", IngredientCategory.DESTILADO);
        ing("Ron", IngredientCategory.DESTILADO);
        ing("Pisco", IngredientCategory.DESTILADO);
        ing("Tequila", IngredientCategory.DESTILADO);
        ing("Whisky", IngredientCategory.DESTILADO);
 
        ing("Triple Sec", IngredientCategory.LICOR);
        ing("Aperitivo amargo", IngredientCategory.LICOR);
 
        ing("Limón", IngredientCategory.FRUTA);
        ing("Frutilla", IngredientCategory.FRUTA);
        ing("Naranja", IngredientCategory.FRUTA);
        ing("Pepino", IngredientCategory.FRUTA);
 
        ing("Jugo de piña", IngredientCategory.JUGO);
        ing("Jugo de naranja", IngredientCategory.JUGO);
        ing("Jugo de arándano", IngredientCategory.JUGO);
 
        ing("Agua tónica", IngredientCategory.MIXER);
        ing("Agua con gas", IngredientCategory.MIXER);
        ing("Ginger ale", IngredientCategory.MIXER);
 
        ing("Hielo", IngredientCategory.OTRO);
        ing("Azúcar", IngredientCategory.OTRO);
        ing("Menta", IngredientCategory.OTRO);
        ing("Clara de huevo", IngredientCategory.OTRO);
    }
 
    private void ing(String name, IngredientCategory category) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setCategory(category);
        ingredients.put(name, ingredientRepository.save(i));
    }
 
    private Map<String, Product> seedProducts() {
        Map<String, Product> map = new HashMap<>();
        map.put("limon", product("Our Sour Limón", "Mezcla lista para servir sabor limón (dato de prueba).", 8990, 50));
        map.put("maracuya", product("Our Sour Maracuyá", "Mezcla lista para servir sabor maracuyá (dato de prueba).", 9490, 30));
        map.put("frutilla", product("Our Sour Frutilla", "Mezcla lista para servir sabor frutilla (dato de prueba).", 9490, 25));
        map.put("pack", product("Pack Degustación x3", "Pack con los tres sabores (dato de prueba).", 24990, 15));
        return map;
    }
 
    private Product product(String name, String description, int price, int stock) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        return productRepository.save(p);
    }
 
    private void seedRecipes(Map<String, Product> products) {
        Product limon = products.get("limon");
        Product maracuya = products.get("maracuya");
        Product frutilla = products.get("frutilla");
 
        recipe("Gin Sour", "Clásico fresco y cítrico.", limon,
                "1. Agrega hielo a una coctelera.\n2. Suma el gin y el Our Sour Limón.\n3. Agita 15 segundos.\n4. Sirve en vaso con hielo.",
                "Gin:60 ml", "Our Sour Limón:30 ml", "Hielo:Al gusto");
 
        recipe("Gin Tonic", "Simple y refrescante.", limon,
                "1. Llena un vaso con hielo.\n2. Agrega el gin.\n3. Completa con agua tónica.\n4. Decora con una rodaja de limón.",
                "Gin:50 ml", "Agua tónica:150 ml", "Limón:1 rodaja", "Hielo:Al gusto");
 
        recipe("Pisco Sour Our Sour", "Versión rápida del pisco sour.", limon,
                "1. Coloca hielo, pisco, Our Sour Limón y clara de huevo en la coctelera.\n2. Agita fuerte por 20 segundos.\n3. Cuela y sirve en copa fría.",
                "Pisco:60 ml", "Our Sour Limón:30 ml", "Clara de huevo:1 unidad", "Hielo:Al gusto");
 
        recipe("Mojito sin alcohol", "Mocktail fresco con menta.", limon,
                "1. Machaca suavemente la menta con azúcar.\n2. Agrega Our Sour Limón y hielo.\n3. Completa con agua con gas.\n4. Mezcla con cuchara.",
                "Our Sour Limón:40 ml", "Agua con gas:120 ml", "Menta:8 hojas", "Azúcar:1 cdta", "Hielo:Al gusto");
 
        recipe("Maracuyá Sour", "Dulce, ácido y tropical.", maracuya,
                "1. Agrega pisco, Our Sour Maracuyá y hielo a la coctelera.\n2. Agita 15 segundos.\n3. Sirve colado.",
                "Pisco:60 ml", "Our Sour Maracuyá:40 ml", "Hielo:Al gusto");
 
        recipe("Frutilla Fizz", "Burbujeante y frutal.", frutilla,
                "1. Llena un vaso alto con hielo.\n2. Agrega vodka y Our Sour Frutilla.\n3. Completa con agua con gas.",
                "Vodka:45 ml", "Our Sour Frutilla:40 ml", "Agua con gas:100 ml", "Hielo:Al gusto");
 
        recipe("Mocktail Tropical", "Sin alcohol, ideal para compartir.", maracuya,
                "1. Mezcla Our Sour Maracuyá y jugo de piña con hielo.\n2. Agita y sirve.\n3. Completa con agua con gas.",
                "Our Sour Maracuyá:40 ml", "Jugo de piña:80 ml", "Agua con gas:60 ml", "Hielo:Al gusto");
 
        recipe("Margarita Our Sour", "Clásico con toque cítrico.", limon,
                "1. Agita tequila, triple sec y Our Sour Limón con hielo.\n2. Sirve en vaso con hielo.",
                "Tequila:50 ml", "Triple Sec:20 ml", "Our Sour Limón:30 ml", "Hielo:Al gusto");
    }
 
    /** Cada item es "NombreIngrediente:cantidad". */
    private void recipe(String title, String description, Product product, String steps, String... items) {
        Recipe r = new Recipe();
        r.setTitle(title);
        r.setDescription(description);
        r.setProduct(product);
        r.setSteps(steps);
        for (String item : items) {
            String[] parts = item.split(":", 2);
            RecipeIngredient ri = new RecipeIngredient();
            ri.setRecipe(r);
            ri.setIngredient(ingredients.get(parts[0]));
            ri.setQuantity(parts[1]);
            r.getIngredients().add(ri);
        }
        recipeRepository.save(r);
    }
}
