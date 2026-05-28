package nl.inholland.recipekeeper.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = false)
public record RecipeCreateRequest(
//        String sourceMealId,

        @NotBlank(message = "Title must not be empty")
        String title,

        @NotBlank(message = "Category must not be empty")
        String category,

        @NotBlank(message = "Area must not be empty")
        String area,
//        String thumbnailPath,

        @NotNull(message = "Ingredients must not be null")
        @Size(min = 1, message = "At least one ingredient is required")
        List<IngredientInput> ingredients,

        @NotNull(message = "Steps must not be empty")
        @Size(min = 1, message = "At least one step is required")
        List<@NotBlank(message = "Step cannot be blank") String> steps,  // FIXME: mismatch (step vs instructions)

        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        Integer rating,
        Boolean cooked
) {}
