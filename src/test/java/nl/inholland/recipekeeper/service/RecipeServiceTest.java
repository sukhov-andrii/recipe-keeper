package nl.inholland.recipekeeper.service;

//import nl.inholland.recipekeeper.entity.factory.RecipeIngredientFactory;
import nl.inholland.recipekeeper.model.entity.Ingredient;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.client.mealdb.MealDbAdapter;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.model.dto.request.IngredientInput;
import nl.inholland.recipekeeper.model.dto.request.RecipeCreateRequest;
import nl.inholland.recipekeeper.exception.domain.RecipeNotFoundException;
//import nl.inholland.recipekeeper.mapper.MealDbMapper;
import nl.inholland.recipekeeper.mapper.RecipeMapper;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock private RecipeRepository recipeRepository;
    @Mock private MealDbRecipeProvider mealDbRecipeProvider;
    @Mock private IngredientService ingredientService;
    @Mock private RecipeMapper recipeMapper;
    @Mock private MealDbAdapter mealDbAdapter;
//    @Mock private RecipeIngredientFactory recipeIngredientFactory;
    @Mock private ImageDownloadService imageDownloadService;
    @Mock private RecipeQueryService recipeQueryService;

    @InjectMocks
    private RecipeService recipeService;

    // ---------------------------
    // CREATE FROM REQUEST
    // ---------------------------

    @Test
    void shouldCreateRecipeFromRequest() {

        Ingredient tomato = new Ingredient("Tomato");

        RecipeCreateRequest request = new RecipeCreateRequest(
                "Pasta",
                "Main",
                "Italian",
                List.of(
                        new IngredientInput("Tomato", "2 pcs")
                ),
                List.of("Boil pasta", "Serve"),
                5,
                false
        );

        when(ingredientService.findOrCreate("Tomato")).thenReturn(tomato);
        when(recipeRepository.save(any(Recipe.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Recipe result = recipeService.createFromRequest(request);

        assertEquals("Pasta", result.getTitle());
        verify(recipeRepository).save(any(Recipe.class));
    }

    // ---------------------------
    // GET BY ID
    // ---------------------------

    @Test
    void shouldReturnRecipeById() {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setTitle("Test");

        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));

        Recipe result = recipeService.getRecipeById(id);

        assertEquals("Test", result.getTitle());
    }

    @Test
    void shouldThrowWhenRecipeNotFound() {
        UUID id = UUID.randomUUID();

        when(recipeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.getRecipeById(id));
    }

    // ---------------------------
    // UPDATE COOKED
    // ---------------------------

    @Test
    void shouldUpdateCooked() {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setCooked(false);

        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Recipe result = recipeService.updateCooked(id, true);

        assertTrue(result.isCooked());
    }

    // ---------------------------
    // UPDATE RATING
    // ---------------------------

    @Test
    void shouldUpdateRating() {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();

        when(recipeRepository.findById(id)).thenReturn(Optional.of(recipe));
        when(recipeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Recipe result = recipeService.updateRating(id, 5);

        assertEquals(5, result.getRating());
    }

    // ---------------------------
    // DELETE
    // ---------------------------

    @Test
    void shouldDeleteRecipe() {
        UUID id = UUID.randomUUID();

        recipeService.deleteRecipe(id);

        verify(recipeRepository).deleteById(id);
    }

    // ---------------------------
    // SEARCH
    // ---------------------------

    @Test
    void shouldReturnQueryResults() {

        Recipe r1 = new Recipe();
        Recipe r2 = new Recipe();

        when(recipeQueryService.query("pasta"))
                .thenReturn(List.of(r1, r2));

        List<Recipe> result = recipeService.query("pasta");

        assertEquals(2, result.size());
        verify(recipeQueryService).query("pasta");
    }

    // ---------------------------
    // UPDATE FAIL CASE
    // ---------------------------

    @Test
    void shouldThrowWhenUpdatingMissingRecipe() {
        UUID id = UUID.randomUUID();

        when(recipeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.updateCooked(id, true));
    }
}