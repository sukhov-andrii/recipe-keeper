package nl.inholland.recipekeeper.mapper;

import nl.inholland.recipekeeper.model.entity.Ingredient;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RecipeIngredient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeMapperTest {

    private final RecipeMapper mapper = new RecipeMapper();

    @Test
    void toDto_should_handle_empty_recipe_safely() {
        Recipe recipe = new Recipe();

        var dto = mapper.toDto(recipe);

        assertNotNull(dto);
        assertTrue(dto.ingredients().isEmpty());
        assertTrue(dto.relatedRecipes().isEmpty());
    }

    @Test
    void toDto_should_map_ingredients_correctly() {
        Recipe recipe = new Recipe();

        Ingredient ing = new Ingredient("tomato");
        RecipeIngredient ri = new RecipeIngredient(recipe, ing, "2");

        recipe.addIngredient(ri);

        var dto = mapper.toDto(recipe);

        assertEquals(1, dto.ingredients().size());
        assertEquals("tomato", dto.ingredients().get(0).name());
        assertEquals("2", dto.ingredients().get(0).measure());
    }

    @Test
    void toDto_should_handle_null_related_recipes() {
        Recipe recipe = new Recipe();
        recipe.setRelatedRecipes(null);

        var dto = mapper.toDto(recipe);

        assertNotNull(dto.relatedRecipes());
        assertTrue(dto.relatedRecipes().isEmpty());
    }
}