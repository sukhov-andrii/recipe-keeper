package nl.inholland.recipekeeper.exception.domain;

public class ConflictingRecipeDefinitionException extends RuntimeException {
    public ConflictingRecipeDefinitionException(String message) {
        super(message);
    }
}
