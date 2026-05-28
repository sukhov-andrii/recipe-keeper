package nl.inholland.recipekeeper.repository;

import nl.inholland.recipekeeper.model.entity.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, UUID> {}
