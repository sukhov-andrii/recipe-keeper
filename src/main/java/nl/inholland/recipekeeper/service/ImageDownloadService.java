package nl.inholland.recipekeeper.service;

import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.client.mealdb.ImageDownloadResult;
import nl.inholland.recipekeeper.exception.external.ImageDownloadException;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageDownloadService {

    private static final int MAX_INGREDIENT_IMAGES = 2;
    private static final Path BASE_DIR = Paths.get("data", "images");

    public ImageDownloadResult downloadAllImages(
            String mainUrl,
            List<String> ingredientNames,
            UUID recipeId
    ) {
        Path dir = createDir(recipeId);

        String mainPath = downloadImage(mainUrl, dir.resolve("main.jpg"));
        List<String> ingredientPaths = downloadIngredientImages(ingredientNames, dir, recipeId);

        return new ImageDownloadResult(mainPath, ingredientPaths);
    }

    private List<String> downloadIngredientImages(
            List<String> ingredientNames,
            Path dir,
            UUID recipeId
    ) {
        List<String> paths = new ArrayList<>();

        int index = 1;

        for (String ingredient : ingredientNames) {

            if (paths.size() >= MAX_INGREDIENT_IMAGES) break;
            if (ingredient == null || ingredient.isBlank()) continue;

            String url = buildUrl(ingredient);
            Path target = dir.resolve("ingredient-" + index + ".png");

            String result = downloadImage(url, target);

            if (result != null) {
                paths.add(result);
            }

            index++;
        }

        return paths;
    }

    private String downloadImage(String url, Path target) {
        try {
            if (url == null || url.isBlank()) return null;

            if (Files.exists(target)) return target.toString();

            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, target);
            }

            return target.toString();

        } catch (Exception e) {
            log.warn("Image download failed: {}", url);
            return null;
        }
    }

    private Path createDir(UUID recipeId) {
        try {
            Path dir = BASE_DIR.resolve(recipeId.toString());
            Files.createDirectories(dir);
            return dir;
        } catch (Exception e) {
            throw new ImageDownloadException("Cannot create directory", e);
        }
    }

    private String buildUrl(String ingredient) {
        return "https://www.themealdb.com/images/ingredients/"
                + ingredient.trim().replace(" ", "%20")
                + ".png";
    }
}
