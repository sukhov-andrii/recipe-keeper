package nl.inholland.recipekeeper.service;

import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.model.entity.entity.Ingredient;
import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import nl.inholland.recipekeeper.model.entity.entity.RecipeIngredient;
import nl.inholland.recipekeeper.model.dto.request.RecipeCreateRequest;

import nl.inholland.recipekeeper.repository.*;
import nl.inholland.recipekeeper.util.TextSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import nl.inholland.recipekeeper.exception.domain.RecipeNotFoundException;

import java.util.*;
import java.util.List;

@Slf4j
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientService ingredientService;


    public RecipeService(RecipeRepository recipeRepository, IngredientService ingredientService) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
    }

    public Page<Recipe> getRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public Recipe getRecipeById(UUID id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found with id " + id));
    }

    @Transactional
    public Recipe createFromRequest(RecipeCreateRequest request) {
        Recipe recipe = new Recipe();

        recipe.setTitle(request.title());
        recipe.setCategory(request.category());
        recipe.setArea(request.area());
        recipe.setSteps(request.steps());
        recipe.setCooked(Boolean.TRUE.equals(request.cooked()));
        recipe.setRating(request.rating());

        if (request.ingredients() != null) {
            request.ingredients().forEach(i -> {
                Ingredient ingredient = ingredientService.findOrCreate(i.name());
                recipe.addIngredient(new RecipeIngredient(recipe, ingredient, i.measure()));
            });
        }

        return recipeRepository.save(recipe);
    }


}