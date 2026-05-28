package nl.inholland.recipekeeper.util;

import java.util.Locale;

public final class TextSanitizer {

    private TextSanitizer() {}

    public static String normalize(String input) {
        if (input == null) return "";
        return input.trim().toLowerCase(Locale.ROOT);
    }

    public static String collapseWhitespace(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\s+", " ");
    }

}