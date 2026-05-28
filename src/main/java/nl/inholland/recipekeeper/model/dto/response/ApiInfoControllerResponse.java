package nl.inholland.recipekeeper.model.dto.response;

public record ApiInfoControllerResponse(
        String service,
        String status,
        String docs
) {}
