package nl.inholland.recipekeeper.service;

import nl.inholland.recipekeeper.model.entity.Recipe;
import nl.inholland.recipekeeper.model.service.RecipeSearchMatcher;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class RecipeQueryServiceTest {

    @Mock RecipeRepository repository;
    @Mock RecipeSearchMatcher matcher;

    @InjectMocks RecipeQueryService service;

    @Test
    void query_should_return_only_matching_recipes() {
        Recipe r1 = new Recipe();
        Recipe r2 = new Recipe();

        when(repository.searchCandidates("pasta"))
                .thenReturn(List.of(r1, r2));

        when(matcher.matches(r1, "pasta")).thenReturn(true);
        when(matcher.matches(r2, "pasta")).thenReturn(false);

        List<Recipe> result = service.query("pasta");

        assertEquals(1, result.size());
        assertTrue(result.contains(r1));
    }

    @Test
    void query_should_return_empty_when_no_matches() {
        Recipe r1 = new Recipe();

        when(repository.searchCandidates("x"))
                .thenReturn(List.of(r1));

        when(matcher.matches(r1, "x")).thenReturn(false);

        List<Recipe> result = service.query("x");

        assertTrue(result.isEmpty());
    }
}