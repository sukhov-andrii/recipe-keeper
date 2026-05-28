package nl.inholland.recipekeeper.model.service;

import nl.inholland.recipekeeper.model.entity.Ingredient;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RecipeIngredient;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RecipeSearchMatcher {

    public boolean matches(Recipe recipe, String query) {
        return matchesTitle(recipe, query) || matchesIngredients(recipe, query);
    }

    private boolean matchesTitle(Recipe recipe, String query) {
        return recipe.getTitle() != null &&
                recipe.getTitle().toLowerCase().contains(query);
    }

    private boolean matchesIngredients(Recipe recipe, String query) {
        return recipe.getRecipeIngredients().stream()
                .map(RecipeIngredient::getIngredient)
                .filter(Objects::nonNull)
                .map(Ingredient::getName)
                .filter(Objects::nonNull)
                .anyMatch(name -> name.contains(query));
    }
}
