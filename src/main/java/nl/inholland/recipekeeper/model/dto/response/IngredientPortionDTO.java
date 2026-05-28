package nl.inholland.recipekeeper.model.dto.response;

import jakarta.validation.constraints.NotBlank;

public record IngredientPortionDTO (
        @NotBlank
        String name,
        @NotBlank
        String measure

){}