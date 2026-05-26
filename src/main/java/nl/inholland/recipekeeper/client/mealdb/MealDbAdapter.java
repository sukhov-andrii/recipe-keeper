package nl.inholland.recipekeeper.client.mealdb;

import com.fasterxml.jackson.databind.JsonNode;
import nl.inholland.recipekeeper.client.RecipeAdapter;
import nl.inholland.recipekeeper.model.dto.response.IngredientDTO;
import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.IntStream;

@Component
public class MealDbAdapter implements RecipeAdapter {

    public Recipe toRecipe(JsonNode meal) {
        Recipe recipe = new Recipe();

        recipe.setSourceMealId(meal.path("idMeal").asText());
        recipe.setTitle(meal.path("strMeal").asText());
        recipe.setCategory(meal.path("strCategory").asText());
        recipe.setArea(meal.path("strArea").asText());
        recipe.setThumbnailPath(meal.path("strMealThumb").asText());

        recipe.setTags(parseTags(meal));
        recipe.setSteps(parseSteps(meal));

        recipe.setCooked(false);
        recipe.setRating(null);

        return recipe;
    }

    public List<IngredientDTO> extractIngredients(JsonNode meal) {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> Map.entry(
                        meal.path("strIngredient" + i).asText(""),
                        meal.path("strMeasure" + i).asText("")
                ))
                .filter(e -> !e.getKey().isBlank())
                .map(e -> new IngredientDTO(
                        e.getKey().trim(),
                        e.getValue().trim()
                ))
                .toList();
    }

    public String extractMainImage(JsonNode meal) {
        return meal.path("strMealThumb").asText(null);
    }

    private List<String> parseTags(JsonNode meal) {
        return Arrays.stream(meal.path("strTags").asText("").split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }

    private List<String> parseSteps(JsonNode meal) {
        String raw = meal.path("strInstructions").asText("");

        List<String> lines = Arrays.stream(raw.split("\\r?\\n"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        if (!lines.isEmpty()) return lines;

        return Arrays.stream(raw.split("(?<=[.!?])\\s+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
    }
}