package nl.inholland.recipekeeper.exception.external;

public class AlreadyImportedRecipeException extends RuntimeException {
    public AlreadyImportedRecipeException(String message) {
        super(message);
    }
}
