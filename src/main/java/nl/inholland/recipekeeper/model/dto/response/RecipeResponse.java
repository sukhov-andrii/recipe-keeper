package nl.inholland.recipekeeper.model.dto.response;

import java.util.List;
import java.util.UUID;

public record RecipeResponse( // RecipeResponse
          UUID id,
          String title,
          String category,
          String area,
          boolean cooked,
          Integer rating,
          List<IngredientPortionDTO> ingredients,
          List<String> tags,
          List<String> steps,
          String thumbnailPath,
          List<String> imagePaths,
          List<RelatedRecipeDTO> relatedRecipes
) {}