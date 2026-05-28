package nl.inholland.recipekeeper.client.mealdb;

import java.util.List;

public record ImageDownloadResult(
        String thumbnailPath,
        List<String> imagePaths
) {}