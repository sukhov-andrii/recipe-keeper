package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.client.mealdb.MealDbAdapter;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.exception.external.AlreadyImportedRecipeException;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import nl.inholland.recipekeeper.model.dto.response.IngredientDTO;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.RecipeIngredient;
import nl.inholland.recipekeeper.model.entity.RelatedRecipe;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class RecipeImportService {

    private final MealDbRecipeProvider mealDbRecipeProvider;
    private final MealDbAdapter mealDbAdapter;
    private final IngredientService ingredientService;
    private final ImageDownloadService imageDownloadService;
    private final RecipeRepository recipeRepository;
    private final RelatedRecipeService relatedRecipeService;
    private final Executor taskExecutor;

    public RecipeImportService(
            MealDbRecipeProvider mealDbRecipeProvider,
            MealDbAdapter mealDbAdapter,
            IngredientService ingredientService,
            ImageDownloadService imageDownloadService,
            RecipeRepository recipeRepository,
            RelatedRecipeService relatedRecipeService,
            Executor taskExecutor
    ) {
        this.mealDbRecipeProvider = mealDbRecipeProvider;
        this.mealDbAdapter = mealDbAdapter;
        this.ingredientService = ingredientService;
        this.imageDownloadService = imageDownloadService;
        this.recipeRepository = recipeRepository;
        this.relatedRecipeService = relatedRecipeService;
        this.taskExecutor = taskExecutor;
    }

    @Transactional
    public Recipe importFromMealDb(String mealName) {

        log.info("Import started: {}", mealName);

        JsonNode meal = mealDbRecipeProvider.getByName(mealName);
        assertNotAlreadyImported(meal);

        Recipe recipe = mealDbAdapter.toRecipe(meal);

        List<IngredientDTO> ingredients = mealDbAdapter.extractIngredients(meal);

        ingredients.forEach(i ->
                recipe.addIngredient(
                        new RecipeIngredient(
                                recipe,
                                ingredientService.findOrCreate(i.name()),
                                i.measure()
                        )
                )
        );

        String category = meal.path("strCategory").asText(null);
        String area = meal.path("strArea").asText(null);

        Recipe saved = recipeRepository.save(recipe);

        // =========================
        // 🔥 CONCURRENCY STARTS HERE
        // =========================

        CompletableFuture<List<RelatedRecipe>> relatedFuture =
                CompletableFuture.supplyAsync(
                        () -> relatedRecipeService.build(recipe, category, area),
                        taskExecutor
                );

        CompletableFuture<ImageDownloadResult> imageFuture =
                CompletableFuture.supplyAsync(
                        () -> imageDownloadService.downloadAllImages(
                                mealDbAdapter.extractMainImage(meal),
                                ingredients.stream()
                                        .map(IngredientDTO::name)
                                        .limit(2)
                                        .toList(),
                                saved.getId()
                        ),
                        taskExecutor
                );

        // wait for both
        List<RelatedRecipe> related = relatedFuture.join();

        try {
            ImageDownloadResult images = imageFuture.join();

//            saved.setRelatedRecipes(related); // FIXME: hibernate fix
            saved.getRelatedRecipes().clear();
            related.forEach(saved::addRelatedRecipe);


            saved.setThumbnailPath(images.thumbnailPath());
            saved.setImagePaths(images.imagePaths());

            return recipeRepository.save(saved);

        } catch (ImageDownloadException e) {
            log.warn("Image pipeline failed for recipe {}", saved.getId(), e);
            saved.setRelatedRecipes(related);
            return recipeRepository.save(saved);
        }
    }

    private void assertNotAlreadyImported(JsonNode meal) {
        String id = meal.get("idMeal").asText();

        recipeRepository.findBySourceMealId(id)
                .ifPresent(r -> {
                    throw new AlreadyImportedRecipeException("Recipe already exists");
                });
    }
}
