package nl.inholland.recipekeeper.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = false)
public record RecipeSummaryResponse(
     UUID id,
     String title,
     String category,
     String area,
     Boolean cooked,
     Integer rating,
     String thumbnailPath
    ) {}