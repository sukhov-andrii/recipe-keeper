package nl.inholland.recipekeeper.repository;

import nl.inholland.recipekeeper.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    Optional<Recipe> findBySourceMealId(String sourceMealId);

    // TODO: verify if that is correct
    @Query("""
    SELECT DISTINCT r FROM Recipe r
    LEFT JOIN r.recipeIngredients ri
    LEFT JOIN ri.ingredient i
    WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%'))
       OR LOWER(i.name) LIKE LOWER(CONCAT('%', :query, '%'))
""")
    List<Recipe> searchCandidates(@Param("query") String query);
}

