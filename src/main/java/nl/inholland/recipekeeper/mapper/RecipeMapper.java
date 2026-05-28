package nl.inholland.recipekeeper.mapper;

import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RelatedRecipe;
import nl.inholland.recipekeeper.model.dto.response.IngredientPortionDTO;
import nl.inholland.recipekeeper.model.dto.response.RecipeResponse;
import nl.inholland.recipekeeper.model.dto.response.RecipeSummaryResponse;
import nl.inholland.recipekeeper.model.dto.response.RelatedRecipeDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecipeMapper {

    public RecipeResponse toDto(Recipe recipe) {

        List<IngredientPortionDTO> ingredientPortions = recipe.getRecipeIngredients()
                .stream()
                .map(ri -> new IngredientPortionDTO(
                        ri.getIngredient().getName(),
                        ri.getMeasure()
                ))
                .toList();

        List<RelatedRecipeDTO> related = recipe.getRelatedRecipes() == null
                ? List.of()
                : recipe.getRelatedRecipes()
                  .stream()
                  .map(this::toRelatedDto)
                  .toList();

        return new RecipeResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCategory(),
                recipe.getArea(),
                recipe.isCooked(),
                recipe.getRating(),
                ingredientPortions,
                recipe.getTags(),
                recipe.getSteps(),
                recipe.getThumbnailPath(),
                recipe.getImagePaths(),
                related
        );
    }

    public RecipeSummaryResponse toSummary(Recipe recipe) {
        return new RecipeSummaryResponse(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getCategory(),
                recipe.getArea(),
                recipe.isCooked(),
                recipe.getRating(),
                recipe.getThumbnailPath()
        );
    }

    private RelatedRecipeDTO toRelatedDto(RelatedRecipe rr) {
        return new RelatedRecipeDTO(
                rr.getRelatedMealId(),
                rr.getRelatedTitle()
        );
    }
}