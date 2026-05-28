package nl.inholland.recipekeeper.service;

import nl.inholland.recipekeeper.exception.domain.BusinessRuleViolationException;
import nl.inholland.recipekeeper.model.entity.Ingredient;
import nl.inholland.recipekeeper.repository.IngredientRepository;
import nl.inholland.recipekeeper.util.TextSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Transactional
    public Ingredient findOrCreate(String name) {
        String normalized = TextSanitizer.normalize(name);

        if (normalized.isBlank()) {
            throw new BusinessRuleViolationException("Ingredient name cannot be empty");
        }

        return ingredientRepository.findByName(normalized)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(normalized)));
    }
}