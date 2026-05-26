package nl.inholland.recipekeeper.model.entity.service;

import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RecipeSearchMatcher {

    public boolean matches(Recipe recipe, String normalizedQuery) {
        return matchesTitle(recipe, normalizedQuery)
                || matchesIngredients(recipe, normalizedQuery);
    }

    private boolean matchesTitle(Recipe recipe, String query) {
        return recipe.getTitle() != null &&
                recipe.getTitle().contains(query);
    }

    private boolean matchesIngredients(Recipe recipe, String query) {
        return recipe.getRecipeIngredients().stream()
                .map(ri -> ri.getIngredient().getName())
                .filter(Objects::nonNull)
                .anyMatch(name -> name.contains(query));
    }
}