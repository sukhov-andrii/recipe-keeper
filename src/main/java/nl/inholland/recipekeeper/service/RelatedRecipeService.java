package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RelatedRecipe;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class RelatedRecipeService {

    private final MealDbRecipeProvider provider;

    public RelatedRecipeService(MealDbRecipeProvider provider) {
        this.provider = provider;
    }

    public List<RelatedRecipe> build(Recipe recipe, String category, String area) {

        Set<RelatedRecipe> related = new LinkedHashSet<>();

        if (category != null) {
            related.addAll(map(provider.filterByCategory(category), recipe));
        }

        if (area != null) {
            related.addAll(map(provider.filterByArea(area), recipe));
        }

        return related.stream()
                .limit(5)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<RelatedRecipe> map(JsonNode node, Recipe recipe) {

        JsonNode meals = node.path("meals");

        if (!meals.isArray()) return List.of();

        return StreamSupport.stream(meals.spliterator(), false)
                .map(m -> new RelatedRecipe(
                        m.path("idMeal").asText(null),
                        m.path("strMeal").asText(null),
                        recipe
                ))
                .filter(r -> r.getRelatedMealId() != null)
                .toList();
    }
}
