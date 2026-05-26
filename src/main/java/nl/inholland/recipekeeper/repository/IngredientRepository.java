package nl.inholland.recipekeeper.repository;

import nl.inholland.recipekeeper.model.entity.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    Optional<Ingredient> findByName(String name);
}