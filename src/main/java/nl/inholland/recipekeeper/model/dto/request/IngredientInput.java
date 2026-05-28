package nl.inholland.recipekeeper.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = false)
public record IngredientInput(
        @NotBlank String name,
        @NotBlank String measure
) {}