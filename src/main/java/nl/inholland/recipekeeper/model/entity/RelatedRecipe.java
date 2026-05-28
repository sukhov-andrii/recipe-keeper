package nl.inholland.recipekeeper.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class RelatedRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String relatedMealId;
    private String relatedTitle;

    @ManyToOne
    private Recipe recipe;

    public RelatedRecipe(String relatedMealId, String relatedTitle, Recipe recipe) {
        this.relatedMealId = relatedMealId;
        this.relatedTitle = relatedTitle;
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelatedRecipe that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
