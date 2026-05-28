package nl.inholland.recipekeeper.client.mealdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.recipekeeper.model.dto.response.IngredientDTO;
import nl.inholland.recipekeeper.model.entity.Recipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MealDbAdapterTest {

    private final MealDbAdapter adapter = new MealDbAdapter();
    private final ObjectMapper mapper = new ObjectMapper();

    private JsonNode json(String raw) throws Exception {
        return mapper.readTree(raw);
    }

    @Test
    void extractIngredients_should_ignore_empty_slots_and_keep_order() throws Exception {
        JsonNode meal = json("""
        {
          "strIngredient1": "Tomato",
          "strMeasure1": "1",
          "strIngredient2": "",
          "strMeasure2": "",
          "strIngredient3": "Onion",
          "strMeasure3": "2"
        }
        """);

        List<IngredientDTO> result = adapter.extractIngredients(meal);

        assertEquals(2, result.size());
        assertEquals("Tomato", result.get(0).name());
        assertEquals("Onion", result.get(1).name());
        assertEquals("1", result.get(0).measure());
        assertEquals("2", result.get(1).measure());
    }

    @Test
    void extractIngredients_should_trim_values() throws Exception {
        JsonNode meal = json("""
        {
          "strIngredient1": "  Garlic  ",
          "strMeasure1": "  3 cloves  "
        }
        """);

        IngredientDTO result = adapter.extractIngredients(meal).get(0);

        assertEquals("Garlic", result.name());
        assertEquals("3 cloves", result.measure());
    }

    @Test
    void extractMainImage_should_return_null_when_missing() throws Exception {
        JsonNode meal = json("{}");

        assertNull(adapter.extractMainImage(meal));
    }

    @Test
    void parseTags_should_split_and_remove_blanks() throws Exception {
        JsonNode meal = json("""
        {
          "strTags": "spicy, vegan, , quick, "
        }
        """);

        var recipe = adapter.toRecipe(meal);

        assertEquals(List.of("spicy", "vegan", "quick"), recipe.getTags());
    }

    @Test
    void parseSteps_should_split_only_by_newlines_and_ignore_blanks() throws Exception {
        JsonNode meal = json("""
        {
          "strInstructions": "Step 1\\n\\nStep 2\\nStep 3"
        }
        """);

        var recipe = adapter.toRecipe(meal);

        assertEquals(List.of("Step 1", "Step 2", "Step 3"), recipe.getSteps());
    }

    @Test
    void toRecipe_shouldMapCoreFields() throws Exception {
        JsonNode meal = json("""
        {
          "idMeal": "123",
          "strMeal": "Pasta",
          "strCategory": "Dinner",
          "strArea": "Italian",
          "strMealThumb": "http://img.jpg",
          "strTags": "easy,fast",
          "strInstructions": "Step 1\\nStep 2"
        }
        """);

        Recipe recipe = adapter.toRecipe(meal);

        assertEquals("123", recipe.getSourceMealId());
        assertEquals("Pasta", recipe.getTitle());
        assertEquals("Dinner", recipe.getCategory());
        assertEquals("Italian", recipe.getArea());
        assertEquals("http://img.jpg", recipe.getThumbnailPath());

        assertEquals(List.of("easy", "fast"), recipe.getTags());
        assertEquals(List.of("Step 1", "Step 2"), recipe.getSteps());
    }
}