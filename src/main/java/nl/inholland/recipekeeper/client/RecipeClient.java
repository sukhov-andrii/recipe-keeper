package nl.inholland.recipekeeper.client;

import com.fasterxml.jackson.databind.JsonNode;

public interface RecipeClient {
    JsonNode get(String pathWithQuery);
}
