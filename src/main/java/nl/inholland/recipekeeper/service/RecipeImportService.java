package nl.inholland.recipekeeper.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.MealDbAdapter;
import nl.inholland.recipekeeper.model.entity.entity.Ingredient;
import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.entity.RecipeIngredient;
import nl.inholland.recipekeeper.model.entity.entity.RelatedRecipe;
import nl.inholland.recipekeeper.model.dto.response.IngredientDTO;
import nl.inholland.recipekeeper.exception.external.AlreadyImportedRecipeException;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.client.mealdb.MealDbAdapter;
import nl.inholland.recipekeeper.client.mealdb.MealDbRecipeProvider;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

// orchestration + decisions
@Slf4j
@Service
public class RecipeImportService {

    private final MealDbRecipeProvider mealDbRecipeProvider;
    private final MealDbAdapter mealDbAdapter;
    private final IngredientService ingredientService;
//    private final ImageDownloadService imageDownloadService;
    private final RecipeRepository recipeRepository;

    public RecipeImportService(MealDbRecipeProvider mealDbRecipeProvider,
                               MealDbAdapter mealDbAdapter,
                               IngredientService ingredientService,
//                               ImageDownloadService imageDownloadService,
                               RecipeRepository recipeRepository) {
        this.mealDbRecipeProvider = mealDbRecipeProvider;
        this.mealDbAdapter = mealDbAdapter;
        this.ingredientService = ingredientService;
//        this.imageDownloadService = imageDownloadService;
        this.recipeRepository = recipeRepository;
    }

    @Transactional
    public Recipe importFromMealDb(String mealName) {

        log.info("Import started for recipe: {}", mealName);

        // 1. Fetch
        // NO ENCODING
        JsonNode meal = mealDbRecipeProvider.getByName(mealName);

        assertNotAlreadyImported(meal);

        // 2. Map
        Recipe recipe = mealDbAdapter.toRecipe(meal);

        // 3. Ingredients
        List<IngredientDTO> ingredients =
                mealDbAdapter.extractIngredients(meal);

        List<RecipeIngredient> recipeIngredients = mapIngredients(recipe, ingredients);
        recipeIngredients.forEach(recipe::addIngredient);

        // 4. Related recipes
        List<RelatedRecipe> related =
                buildRelatedRecipesFromMealDb(recipe, meal);

        recipe.setRelatedRecipes(related);

        // 5. Persist FIRST
        Recipe saved = recipeRepository.save(recipe);

//        // 6. Filesystem side effects AFTER persistence. Images are best-effort, but dont fail import
//        // FIXME: bad architecture, double save call to database, reliance on JPA transaction
//        try {
////            imageDownloadService.downloadAllImages(saved, meal);
//
//            String mainUrl = mealDbAdapter.extractMainImage(meal);
//
//            List<String> ingredientNames = mealDbAdapter.extractIngredients(meal)
//                    .stream()
//                    .map(IngredientDTO::name)
//                    .toList();
//
//            ImageDownloadResult images =
//                    imageDownloadService.downloadAllImages(mainUrl, ingredientNames, saved.getId());
//
//            saved.setThumbnailPath(images.thumbnailPath());
//            saved.setImagePaths(images.imagePaths());
//        } catch (ImageDownloadException e) {
//            log.warn("Image pipeline failed for recipe {} - continuing import", saved.getId(), e);
//        }



//        recipeRepository.save(saved);

        log.info("Import completed: id={}, title={}", saved.getId(), saved.getTitle());

        return saved;
    }

    private List<RecipeIngredient> mapIngredients(Recipe recipe, List<IngredientDTO> ingredients) {
        return ingredients.stream()
                .map(i -> {
                    Ingredient ingredient = ingredientService.findOrCreate(i.name());

                    return new RecipeIngredient(
                            recipe,
                            ingredient,
                            i.measure()
                    );
                })
                .toList();
    }

    private List<RelatedRecipe> buildRelatedRecipesFromMealDb(Recipe recipe, JsonNode meal) {

        String category = meal.path("strCategory").asText(null);
        String area = meal.path("strArea").asText(null);

        JsonNode byCategory = mealDbRecipeProvider.filterByCategory(category);
        JsonNode byArea = mealDbRecipeProvider.filterByArea(area);

        Set<RelatedRecipe> related = new LinkedHashSet<>();

        related.addAll(mapRelated(byCategory, recipe));
        related.addAll(mapRelated(byArea, recipe));

        return related.stream()
                .limit(5)
                .toList();
    }

    private List<RelatedRecipe> mapRelated(JsonNode node, Recipe recipe) {

        JsonNode meals = node.path("meals");

        if (!meals.isArray() || meals.isEmpty()) {
            return List.of();
        }

        return StreamSupport.stream(meals.spliterator(), false)
                .map(m -> new RelatedRecipe(
                        m.path("idMeal").asText(null),
                        m.path("strMeal").asText(null),
                        recipe
                ))
                .filter(r -> r.getRelatedMealId() != null && r.getRelatedTitle() != null)
                .toList();
    }

    // FIXME: possibly add a dedicated method to repository
    private void assertNotAlreadyImported(JsonNode meal) {
        String id = meal.get("idMeal").asText();

        recipeRepository.findBySourceMealId(id)
                .ifPresent(r -> { throw new AlreadyImportedRecipeException("Recipe already exists"); });
    }
}
