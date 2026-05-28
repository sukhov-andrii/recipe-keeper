package nl.inholland.recipekeeper.service;

import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    private String downloadMainImage(String url, Path recipeDir, UUID recipeId) {
        if (url == null || url.isBlank()) {
            log.warn("Image missing (type=main, recipe={})", recipeId);
            return null;
        }

        Path target = recipeDir.resolve("main.jpg");
        return downloadImage(url, target);
    }

    // FIXME: now method unnecessarily takes Recipe object and title object. but in needs id and name (for logs and file naming)
    private List<String> downloadIngredientImages(List<String> ingredientNames, Path recipeDir, UUID recipeId) {
        List<String> paths = new ArrayList<>();

        int index = 1;

        for (String ingredient : ingredientNames) {

            if (paths.size() >= MAX_INGREDIENT_IMAGES) break;
            if (ingredient == null || ingredient.isBlank()) continue;

            String url = buildIngredientUrl(ingredient);
            String safeName = "ingredient-" + index + "-" + recipeId + ".png";
            Path target = recipeDir.resolve(safeName);


            try {
                String path = downloadImage(url, target);
                if (path != null) paths.add(path);
            } catch (ImageDownloadException e) {
                log.warn("Failed ingredient image: {}", ingredient);
            }

            index++;
        }

        return paths;
    }

    private Path createRecipeDir(UUID recipeId) {
        if (recipeId == null) {
            throw new ImageDownloadException(
                    "Recipe ID is null. Persist recipe before calling image pipeline."
            );
        }

        Path dir = BASE_DIR.resolve(recipeId.toString());
        try {
            Files.createDirectories(dir);
        } catch (Exception e) {
            throw new ImageDownloadException("Could not create image directory", e);
        }
        return dir;
    }

    private String buildIngredientUrl(String ingredient) {
        String clean = ingredient.trim().replace(" ", "%20");

        return "https://www.themealdb.com/images/ingredients/"
                + clean
                + ".png";
    }

    private String downloadImage(String url, Path target) {
        try {
            if (Files.exists(target)) {
                log.info("Image skipped (already exists): {}", target);
                return target.toString();
            }

            try (InputStream in = new URL(url).openStream()) {  // TODO: replace with URI
                Files.copy(in, target);
            }

            log.info("Image downloaded: {} -> {}", url, target);
            return target.toString();

        } catch (IOException e) {
            throw new ImageDownloadException("Failed to download image", e);
        }
    }
}
