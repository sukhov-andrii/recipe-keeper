package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.recipekeeper.client.mealdb.*;
import nl.inholland.recipekeeper.exception.external.AlreadyImportedRecipeException;
import nl.inholland.recipekeeper.exception.external.ExternalAmbiguousRecipeException;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RecipeImportServiceTest {

    @Mock MealDbRecipeProvider provider;
    @Mock MealDbAdapter adapter;
    @Mock IngredientService ingredientService;
    @Mock ImageDownloadService imageService;
    @Mock RecipeRepository repository;

    @InjectMocks RecipeImportService service;

    @Test
    void should_throw_when_recipe_already_imported() {
        JsonNode meal = mock(JsonNode.class);
        JsonNode idNode = mock(JsonNode.class);

        when(provider.getByName("pasta")).thenReturn(meal);
        when(meal.get("idMeal")).thenReturn(idNode);
        when(idNode.asText()).thenReturn("123");

        when(repository.findBySourceMealId("123"))
                .thenReturn(Optional.of(new Recipe()));

        assertThrows(AlreadyImportedRecipeException.class,
                () -> service.importFromMealDb("pasta"));
    }

    @Test
    void should_throw_when_ambiguous() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        JsonNode meal = mapper.readTree("""
    {
      "meals": [
        { "strMeal": "A" },
        { "strMeal": "B" }
      ]
    }
    """);

        when(provider.getByName("pasta")).thenReturn(meal);

        assertThrows(ExternalAmbiguousRecipeException.class,
                () -> service.importFromMealDb("pasta"));
    }

    @Test
    void should_allow_import_when_not_existing() {

        JsonNode meal = mock(JsonNode.class);
        JsonNode idNode = mock(JsonNode.class);
        JsonNode categoryNode = mock(JsonNode.class);
        JsonNode areaNode = mock(JsonNode.class);

        when(provider.getByName("pasta")).thenReturn(meal);

        when(meal.get("idMeal")).thenReturn(idNode);
        when(idNode.asText()).thenReturn("123");

        when(meal.path("strCategory")).thenReturn(categoryNode);
        when(meal.path("strArea")).thenReturn(areaNode);

        when(categoryNode.asText(null)).thenReturn("Seafood");
        when(areaNode.asText(null)).thenReturn("Italian");

        when(repository.findBySourceMealId("123"))
                .thenReturn(Optional.empty());

        when(repository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(adapter.toRecipe(meal)).thenReturn(new Recipe());

        // related builder isolation (important)
        when(provider.filterByCategory("Seafood"))
                .thenReturn(mock(JsonNode.class));

        when(provider.filterByArea("Italian"))
                .thenReturn(mock(JsonNode.class));

        JsonNode emptyMeals = mock(JsonNode.class);
        when(emptyMeals.path("meals")).thenReturn(mock(JsonNode.class));

        when(provider.filterByCategory("Seafood").path("meals"))
                .thenReturn(emptyMeals);

        when(provider.filterByArea("Italian").path("meals"))
                .thenReturn(emptyMeals);

        when(emptyMeals.isArray()).thenReturn(true);
        when(emptyMeals.isEmpty()).thenReturn(true);

        service.importFromMealDb("pasta");

        verify(repository).save(any());
    }
}