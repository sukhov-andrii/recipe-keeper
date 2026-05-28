package nl.inholland.recipekeeper.model.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = false)
public record PatchCookedRequest(
        @NotNull(message = "Cooked field is required")
        Boolean cooked
) {}