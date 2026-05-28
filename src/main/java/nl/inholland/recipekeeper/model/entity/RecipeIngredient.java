package nl.inholland.recipekeeper.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Objects;
import java.util.UUID;


@Entity
public class RecipeIngredient {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JsonIgnore
    private Recipe recipe;

    @ManyToOne
    private Ingredient ingredient;

    private String measure;

//    @Version
//    private Long version;

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, String measure) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.measure = measure;
    }

    public RecipeIngredient() {

    }

    public UUID getId() { return id; }
    public Recipe getRecipe() { return recipe; }
    public Ingredient getIngredient() { return ingredient; }
    public String getMeasure() { return measure; }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecipeIngredient)) return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(recipe, that.recipe) &&
                Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipe, ingredient);
    }
}