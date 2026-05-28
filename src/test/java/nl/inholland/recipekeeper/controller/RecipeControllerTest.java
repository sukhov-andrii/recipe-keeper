package nl.inholland.recipekeeper.controller;

import nl.inholland.recipekeeper.model.dto.request.RecipeCreateRequest;
import nl.inholland.recipekeeper.exception.domain.RecipeNotFoundException;
import nl.inholland.recipekeeper.mapper.RecipeMapper;
import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeService recipeService;

    @MockitoBean
    private RecipeMapper recipeMapper;

    // -------------------------
    // GET paginated recipes
    // -------------------------
    @Test
    void shouldReturnPagedRecipes() throws Exception {
        Recipe r1 = new Recipe();
        r1.setTitle("Pasta");

        Recipe r2 = new Recipe();
        r2.setTitle("Salad");

        var pageable = PageRequest.of(0, 20);

        Mockito.when(recipeService.getRecipes(pageable))
                .thenReturn(new PageImpl<>(List.of(r1, r2), pageable, 2));

        mockMvc.perform(get("/recipes?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"));
    }

    // -------------------------
    // SEARCH validation
    // -------------------------
    @Test
    void shouldReturn400WhenSearchQueryBlank() throws Exception {
        mockMvc.perform(get("/recipes/search?query="))
                .andExpect(status().isBadRequest());
    }

    // -------------------------
    // GET by ID
    // -------------------------
    @Test
    void shouldReturnRecipeById() throws Exception {
        UUID id = UUID.randomUUID();

        Recipe recipe = new Recipe();
        recipe.setTitle("Pasta");

        Mockito.when(recipeService.getRecipeById(id)).thenReturn(recipe);

        mockMvc.perform(get("/recipes/" + id))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn404WhenRecipeNotFound() throws Exception {
        UUID id = UUID.randomUUID();

        Mockito.when(recipeService.getRecipeById(id))
                .thenThrow(new RecipeNotFoundException(id.toString()));

        mockMvc.perform(get("/recipes/" + id))
                .andExpect(status().isNotFound());
    }

    // -------------------------
    // IMPORT
    // -------------------------
    @Test
    void shouldReturn400WhenImportNameBlank() throws Exception {
        mockMvc.perform(post("/recipes/import"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldImportRecipe() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setTitle("Arrabiata");

        Mockito.when(recipeService.importFromMealDb("Arrabiata"))
                .thenReturn(recipe);

        mockMvc.perform(post("/recipes/import?name=Arrabiata"))
                .andExpect(status().isCreated());
    }

    // -------------------------
    // CREATE
    // -------------------------
    @Test
    void shouldReturn400WhenCreateRequestInvalid() throws Exception {
        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "Pasta"
                        }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreateRequestEmptyBody() throws Exception {
        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateRecipe() throws Exception {
        Recipe saved = new Recipe();
        saved.setTitle("Pasta");

        Mockito.when(recipeService.createFromRequest(Mockito.any(RecipeCreateRequest.class)))
                .thenReturn(saved);

        mockMvc.perform(post("/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "title": "Pasta",
                          "category": "Main",
                          "area": "Italian",
                          "steps": [
                            "Boil pasta",
                            "Add sauce"
                          ],
                          "ingredients": [
                            {
                              "name": "Pasta",
                              "measure": "200g"
                            },
                            {
                              "name": "Tomato",
                              "measure": "2 pcs"
                            }
                          ]
                        }
                    """))
                .andExpect(status().isCreated());
    }

    // -------------------------
    // DELETE
    // -------------------------
    @Test
    void shouldDeleteRecipe() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/recipes/" + id))
                .andExpect(status().isNoContent());

        Mockito.verify(recipeService).deleteRecipe(id);
    }

    // -------------------------
    // PATCH cooked
    // -------------------------
    @Test
    void shouldUpdateCooked() throws Exception {
        UUID id = UUID.randomUUID();

        Recipe updated = new Recipe();
        updated.setCooked(true);

        Mockito.when(recipeService.updateCooked(id, true))
                .thenReturn(updated);

        mockMvc.perform(patch("/recipes/" + id + "/cooked")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "cooked": true
                            }
                        """))
                .andExpect(status().isOk());
    }

    // -------------------------
    // PATCH rating validation
    // -------------------------
    @Test
    void shouldReturn400WhenRatingInvalid() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(patch("/recipes/" + id + "/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "rating": 6
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    // -------------------------
    // PATCH rating
    // -------------------------
    @Test
    void shouldUpdateRating() throws Exception {
        UUID id = UUID.randomUUID();

        Recipe updated = new Recipe();
        updated.setRating(5);

        Mockito.when(recipeService.updateRating(id, 5))
                .thenReturn(updated);

        mockMvc.perform(patch("/recipes/" + id + "/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "rating": 5
                            }
                        """))
                .andExpect(status().isOk());
    }
}