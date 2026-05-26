package nl.inholland.recipekeeper.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import nl.inholland.recipekeeper.model.dto.request.PatchCookedRequest;
import nl.inholland.recipekeeper.model.dto.request.PatchRatingRequest;
import nl.inholland.recipekeeper.model.dto.request.RecipeCreateRequest;
import nl.inholland.recipekeeper.model.dto.response.RecipeResponse;
import nl.inholland.recipekeeper.model.dto.response.RecipeSummaryResponse;
import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import nl.inholland.recipekeeper.mapper.RecipeMapper;
import nl.inholland.recipekeeper.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.recipeMapper = recipeMapper;
    }

    @GetMapping
    public ResponseEntity<Page<RecipeSummaryResponse>> getRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<Recipe> recipesPage = recipeService.getRecipes(PageRequest.of(page, size));
        Page<RecipeSummaryResponse> summaryPage = recipesPage.map(recipeMapper::toSummary);

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(summaryPage.getTotalElements()))
                .body(summaryPage);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<RecipeSummaryResponse>> search(@RequestParam @NotBlank String query) {
//        List<Recipe> result = recipeService.query(query);
//        return ResponseEntity.ok(result.stream()
//                .map(recipeMapper::toSummary)
//                .toList());
//    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipe(@PathVariable UUID id) {
        Recipe recipe = recipeService.getRecipeById(id);
        return ResponseEntity.ok(recipeMapper.toDto(recipe));
    }

    @PostMapping("/import")
    public ResponseEntity<RecipeResponse> importRecipe(@RequestParam @NotBlank String name) {
        Recipe recipe = recipeService.importFromMealDb(name);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeMapper.toDto(recipe));
    }

    @PostMapping
    public ResponseEntity<RecipeResponse> addRecipe(@Valid @RequestBody RecipeCreateRequest request) {
        Recipe saved = recipeService.createFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recipeMapper.toDto(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable UUID id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cooked")
    public ResponseEntity<RecipeResponse> updateCooked(
            @PathVariable UUID id,
            @Valid @RequestBody PatchCookedRequest request
    ) {
        Recipe updated = recipeService.updateCooked(id, request.cooked());
        return ResponseEntity.ok(recipeMapper.toDto(updated));
    }

    @PatchMapping("/{id}/rating")
    public ResponseEntity<RecipeResponse> updateRating(
            @PathVariable UUID id,
            @Valid @RequestBody PatchRatingRequest request
    ) {
        Recipe updated = recipeService.updateRating(id, request.rating());
        return ResponseEntity.ok(recipeMapper.toDto(updated));
    }
}