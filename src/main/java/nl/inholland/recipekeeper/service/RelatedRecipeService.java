package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RelatedRecipe;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class RelatedRecipeService {

    private final MealDbRecipeProvider provider;

    public RelatedRecipeService(MealDbRecipeProvider provider) {
        this.provider = provider;
    }

    public List<RelatedRecipe> build(Recipe recipe, String category, String area) {

        Set<RelatedRecipe> related = new LinkedHashSet<>();

        log.info("Building related recipes for recipe={}", recipe.getTitle());
        log.info("Input category={}, area={}", category, area);

        // 1. category-based
        if (category != null && !category.isBlank()) {
            related.addAll(map(provider.filterByCategory(category), recipe));
        }

        // 2. area-based
        if (area != null && !area.isBlank()) {
            related.addAll(map(provider.filterByArea(area), recipe));
        }

        // 3. fallback if empty → relax constraint
        if (related.isEmpty() && category != null) {
            log.info("Fallback: retrying broader category search only");
            related.addAll(map(provider.filterByCategory(category), recipe));
        }

        if (related.isEmpty()) {
            log.info("Fallback: using ingredient-based related recipes");

            String ingredient = recipe.getRecipeIngredients()
                    .stream()
                    .findFirst()
                    .map(ri -> ri.getIngredient().getName())
                    .orElse(null);

            if (ingredient != null) {
                related.addAll(map(provider.filterByIngredient(ingredient), recipe));
            }
        }

        log.info("Related recipes found: {}", related.size());

        return related.stream()
                .limit(5)
                .toList();
    }

    private List<RelatedRecipe> map(JsonNode node, Recipe recipe) {

        JsonNode meals;

        if (node.isArray()) {
            meals = node;
        } else {
            meals = node.path("meals");
        }

        if (meals == null || !meals.isArray() || meals.isEmpty()) {
            return List.of();
        }

        return StreamSupport.stream(meals.spliterator(), false)
                .map(m -> {
                    RelatedRecipe rr = new RelatedRecipe(
                            m.path("idMeal").asText(null),
                            m.path("strMeal").asText(null),
                            recipe
                    );
                    rr.setRecipe(recipe); // redundant but harmless
                    return rr;
                })
                .filter(r -> r.getRelatedMealId() != null)
                .filter(r -> !r.getRelatedMealId().equals(recipe.getSourceMealId()))
                .toList();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
