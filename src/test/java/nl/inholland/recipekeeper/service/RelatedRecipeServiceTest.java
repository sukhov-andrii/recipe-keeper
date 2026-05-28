package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RelatedRecipeServiceTest {

    MealDbRecipeProvider provider = mock(MealDbRecipeProvider.class);
    RelatedRecipeService relatedRecipe = new RelatedRecipeService(provider);

    ObjectMapper mapper = new ObjectMapper();

    private JsonNode json(String raw) throws Exception {
        return mapper.readTree(raw);
    }

    @Test
    void build_should_merge_category_and_area_results() throws Exception {

        Recipe recipe = new Recipe();

        JsonNode categoryNode = json("""
        {
          "meals": [
            { "idMeal": "1", "strMeal": "A" }
          ]
        }
        """);

        JsonNode areaNode = json("""
        {
          "meals": [
            { "idMeal": "2", "strMeal": "B" }
          ]
        }
        """);

        when(provider.filterByCategory("Seafood")).thenReturn(categoryNode);
        when(provider.filterByArea("Italian")).thenReturn(areaNode);

        List<?> result = relatedRecipe.build(recipe, "Seafood", "Italian");

        assertEquals(2, result.size());
    }

    @Test
    void build_should_return_empty_when_both_null() {

        Recipe recipe = new Recipe();

        List<?> result = relatedRecipe.build(recipe, null, null);

        assertTrue(result.isEmpty());
    }
}