package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.MealDbAdapter;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RecipeIngredient;
import nl.inholland.recipekeeper.model.dto.response.IngredientDTO;
import nl.inholland.recipekeeper.exception.external.AlreadyImportedRecipeException;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// orchestration + decisions
@Slf4j
@Service
public class RecipeImportService {

    private final MealDbRecipeProvider mealDbRecipeProvider;
    private final MealDbAdapter mealDbAdapter;
    private final IngredientService ingredientService;
    private final ImageDownloadService imageDownloadService;
    private final RecipeRepository recipeRepository;
    private final RelatedRecipeService relatedRecipeService;

    public RecipeImportService(MealDbRecipeProvider mealDbRecipeProvider,
                               MealDbAdapter mealDbAdapter,
                               IngredientService ingredientService,
                               ImageDownloadService imageDownloadService,
                               RecipeRepository recipeRepository, RelatedRecipeService relatedRecipeService) {
        this.mealDbRecipeProvider = mealDbRecipeProvider;
        this.mealDbAdapter = mealDbAdapter;
        this.ingredientService = ingredientService;
        this.imageDownloadService = imageDownloadService;
        this.recipeRepository = recipeRepository;
        this.relatedRecipeService = relatedRecipeService;

    }

    @Transactional
    public Recipe importFromMealDb(String mealName) {
        log.info("Import started for recipe: {}", mealName);

        // 1. Fetch (NO ENCODING)
        JsonNode meal = mealDbRecipeProvider.getByName(mealName);

        assertNotAlreadyImported(meal);

        // 2. Map
        Recipe recipe = mealDbAdapter.toRecipe(meal);

        // 3. Ingredients
        List<IngredientDTO> ingredients =
                mealDbAdapter.extractIngredients(meal);

        ingredients.forEach(i ->
                recipe.addIngredient(
                        new RecipeIngredient(
                                recipe,
                                ingredientService.findOrCreate(i.name()),
                                i.measure()
                        )
                )
        );

        // 4. Related recipes

        String category = meal.path("strCategory").asText(null);
        String area = meal.path("strArea").asText(null);

        recipe.setRelatedRecipes(
                relatedRecipeService.build(recipe, category, area)
        );

        // 5. Persist FIRST
        Recipe saved = recipeRepository.save(recipe);

        // 6. Filesystem side effects AFTER persistence. Images are best-effort, but dont fail import
        try {
            ImageDownloadResult images = imageDownloadService.downloadAllImages(
                    mealDbAdapter.extractMainImage(meal),
                    ingredients.stream().map(IngredientDTO::name).toList(),
                    saved.getId()
            );

            saved.setThumbnailPath(images.thumbnailPath());
            saved.setImagePaths(images.imagePaths());

            recipeRepository.save(saved);

        } catch (ImageDownloadException e) {
            log.warn("Image pipeline failed for recipe {}", saved.getId(), e);
        }

        log.info("Import completed: id={}, title={}", saved.getId(), saved.getTitle());

        return saved;
    }


    // FIXME: possibly add a dedicated method to repository
    private void assertNotAlreadyImported(JsonNode meal) {
        String id = meal.get("idMeal").asText();

        recipeRepository.findBySourceMealId(id)
                .ifPresent(r -> { throw new AlreadyImportedRecipeException("Recipe already exists"); });
    }
}
