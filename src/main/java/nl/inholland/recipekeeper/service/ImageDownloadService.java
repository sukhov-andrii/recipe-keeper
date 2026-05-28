package nl.inholland.recipekeeper.service;

import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// IO + filesystem + downloads
@Slf4j
@Service
public class ImageDownloadService {

    private static final int MAX_INGREDIENT_IMAGES = 2; // 2; // business rule

    private static final Path BASE_DIR = Paths.get("data", "images");

    // FIXME: decouple method from JsonNode
    public ImageDownloadResult downloadAllImages(String mainUrl, List<String> ingredientNames, UUID recipeId) {
        Path recipeDir = createRecipeDir(recipeId);

        String mainPath = downloadMainImage(mainUrl, recipeDir, recipeId);
        List<String> ingredientPaths = downloadIngredientImages(ingredientNames, recipeDir, recipeId);

        return new ImageDownloadResult(mainPath, ingredientPaths);
    }

    private String downloadMainImage(String url, Path dir, UUID recipeId) {
        if (url == null || url.isBlank()) return null;

        return downloadImage(url, dir.resolve("main.jpg"));
    }
    private List<String> downloadIngredientImages(List<String> ingredients, Path dir, UUID recipeId) {

        List<String> paths = new ArrayList<>();
        int index = 1;

        for (String ingredient : ingredients) {

            if (paths.size() >= MAX_INGREDIENT_IMAGES) break;
            if (ingredient == null || ingredient.isBlank()) continue;

            String url = buildUrl(ingredient);
            Path target = dir.resolve("ingredient-" + index + "-" + recipeId + ".png");


            try {
                paths.add(downloadImage(url, target));
            } catch (ImageDownloadException e) {
                log.warn("Ingredient image failed: {}", ingredient);
            }

            index++;
        }

        return paths;
    }

    private Path createRecipeDir(UUID recipeId) {
        try {
            Path dir = BASE_DIR.resolve(recipeId.toString());
            Files.createDirectories(dir);
            return dir;
        } catch (Exception e) {
            throw new ImageDownloadException("Could not create image directory", e);
        }
    }


    private String buildUrl(String ingredient) {
        return "https://www.themealdb.com/images/ingredients/"
                + ingredient.trim().replace(" ", "%20")
                + ".png";
    }

    private String downloadImage(String url, Path target) {
        try {
            if (Files.exists(target)) {
                log.info("Image skipped (already exists): {}", target);
                return target.toString();
            }

            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, target);
            }

            log.info("Image downloaded: {} -> {}", url, target);
            return target.toString();

        } catch (IOException e) {
            throw new ImageDownloadException("Failed to download image", e);
        }
    }
}
