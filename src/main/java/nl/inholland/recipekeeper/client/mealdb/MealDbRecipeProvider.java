package nl.inholland.recipekeeper.client.mealdb;

import com.fasterxml.jackson.databind.JsonNode;
import nl.inholland.recipekeeper.client.RecipeProvider;
import nl.inholland.recipekeeper.exception.domain.RecipeNotFoundException;
import nl.inholland.recipekeeper.exception.external.ExternalAmbiguousRecipeException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class MealDbRecipeProvider implements RecipeProvider {

    private final MealDbClient client;

    public MealDbRecipeProvider(MealDbClient client) {
        this.client = client;
    }

    // --- API ---

    public JsonNode getByName(String name) {
        JsonNode meals = client.get("/search.php?s=" + name.trim())
                .path("meals");

        if (!isValid(meals)) {
            throw new RecipeNotFoundException("No recipe found for: " + name);
        }

        return extractSingle(meals, name);
    }

    public JsonNode getById(String id) {
        return client.get("/lookup.php?i=" + id)
                .path("meals");
    }

    public JsonNode filterByCategory(String category) {
        return client.get("/filter.php?c=" + category)
                .path("meals");
    }

    public JsonNode filterByArea(String area) {
        return client.get("/filter.php?a=" + area)
                .path("meals");
    }

    public JsonNode filterByIngredient(String ingredient) {
        return client.get("/filter.php?i=" + ingredient)
                .path("meals");
    }

    // --- internal ---

    private boolean isValid(JsonNode meals) {
        return meals != null && meals.isArray() && !meals.isEmpty();
    }

    private JsonNode extractSingle(JsonNode meals, String originalQuery) {

        if (meals.size() == 1) return meals.get(0);

        for (JsonNode meal : meals) {
            if (meal.path("strMeal").asText()
                    .equalsIgnoreCase(originalQuery.trim())) {
                return meal;
            }
        }

        List<String> alternatives = StreamSupport.stream(meals.spliterator(), false)
                .map(m -> m.path("strMeal").asText())
                .toList();

        throw new ExternalAmbiguousRecipeException(
                "Multiple recipes found (no exact match)", alternatives
        );
    }
}