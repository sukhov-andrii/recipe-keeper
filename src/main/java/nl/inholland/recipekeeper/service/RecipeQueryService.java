package nl.inholland.recipekeeper.service;

import nl.inholland.recipekeeper.model.entity.service.RecipeSearchMatcher;
import nl.inholland.recipekeeper.model.entity.entity.Recipe;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeQueryService {

    private final RecipeRepository repository;
    private final RecipeSearchMatcher matcher;

    public RecipeQueryService(RecipeRepository repository, RecipeSearchMatcher matcher) {
        this.repository = repository;
        this.matcher = matcher;
    }

    // FIXME: search uses full repository scan
    // FIXME: it is filter, not "search"
    // You are doing: “retrieve many, think locally”
    //instead of:
    //“ask DB precisely, think minimally”
    public List<Recipe> search(String query) {
        return repository.searchCandidates(query)
                .stream()
                .filter(r -> matcher.matches(r, query))
                .toList();
    }
}