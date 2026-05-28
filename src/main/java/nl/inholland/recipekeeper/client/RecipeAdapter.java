package nl.inholland.recipekeeper.client;

import com.fasterxml.jackson.databind.JsonNode;
import nl.inholland.recipekeeper.model.entity.Recipe;

// JSON → entity data
public interface RecipeAdapter {
    Recipe toRecipe(JsonNode meal);
}
