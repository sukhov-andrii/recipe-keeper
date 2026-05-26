package nl.inholland.recipekeeper.model.entity.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private Instant createdAt;  // or Timestamps
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
    private List<String> imagePaths = new ArrayList<>(); // FIXME: possibly store urls, not just Paths

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

//    @Version
//    private Long version;

    public Recipe() {
    }

    public Recipe(String title) {
        this();
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

    public UUID getId() {
        return id;
    }

    public String getSourceMealId() {
        return sourceMealId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getArea() {
        return area;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<String> getSteps() {
        return steps;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public boolean isCooked() {
        return cooked;
    }

    public Integer getRating() {
        return rating;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public void setSourceMealId(String sourceMealId) {
        this.sourceMealId = sourceMealId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // FIXME: 2 entry points
    public void setRating(Integer rating) {
        if (rating == null) {
            this.rating = null;
            return;
        }

        this.rating = rating;
    }

    // FIXME: 2 entry points
    public void setCooked(boolean cooked) {
        this.cooked = cooked;
    }

    public void addIngredient(RecipeIngredient ri) {
        recipeIngredients.add(ri);
        ri.setRecipe(this);
    }

//    public void markAsCooked(){
//        this.cooked = true;
//    }
//    public void rate(int rating){
//        if (rating < 1 || rating > 5) {
//            throw new IllegalArgumentException("Rating must be between 1 and 5");
//        }
//        this.rating = rating;
//    }

    public void addTag(String tag){
        this.tags.add(tag);
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }


    public List<RelatedRecipe> getRelatedRecipes() {
        return relatedRecipes;
    }

    public void setRelatedRecipes(List<RelatedRecipe> relatedRecipes) {
        this.relatedRecipes = relatedRecipes;
    }
}