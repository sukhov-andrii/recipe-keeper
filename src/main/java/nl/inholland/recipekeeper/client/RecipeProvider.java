package nl.inholland.recipekeeper.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface RecipeProvider {
    JsonNode getByName(String name);

    JsonNode getById(String id);

    JsonNode filterByCategory(String category);

    JsonNode filterByArea(String area);
}
