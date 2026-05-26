package nl.inholland.recipekeeper.exception.external;

import java.util.List;

public class ExternalAmbiguousRecipeException extends RuntimeException {

    private final List<String> alternatives;

    public ExternalAmbiguousRecipeException(String message, List<String> alternatives) {
        super(message);
        this.alternatives = alternatives;
    }

    public List<String> getAlternatives() {
        return alternatives;
    }
}