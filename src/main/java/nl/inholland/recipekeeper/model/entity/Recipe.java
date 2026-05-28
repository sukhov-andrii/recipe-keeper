package nl.inholland.recipekeeper.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String sourceMealId;

    private String title;
    private String category;
    private String area;

    private boolean cooked;
    private Integer rating;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RelatedRecipe> relatedRecipes = new ArrayList<>();

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(columnDefinition = "TEXT")
    private List<String> steps = new ArrayList<>();

    private String thumbnailPath;

    @ElementCollection
    private List<String> imagePaths = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    public Recipe(String title) {
        this.title = title;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void addIngredient(RecipeIngredient ri) {
        recipeIngredients.add(ri);
        ri.setRecipe(this);
    }

    public void addTag(String tag) {
        this.tags.add(tag);
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags);
    }

    public void setSteps(List<String> steps) {
        this.steps = new ArrayList<>(steps);
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = new ArrayList<>(imagePaths);
    }

    public void setRelatedRecipes(List<RelatedRecipe> relatedRecipes) {
        this.relatedRecipes = new ArrayList<>(relatedRecipes);
    }

    public void addRelatedRecipe(RelatedRecipe rr) {
        relatedRecipes.add(rr);
        rr.setRecipe(this);
    }
}
