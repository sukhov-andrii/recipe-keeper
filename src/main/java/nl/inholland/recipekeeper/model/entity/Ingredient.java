package nl.inholland.recipekeeper.model.entity;

import jakarta.persistence.*;
import java.util.UUID;


@Entity
public class Ingredient {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String name;

//    @Version
//    private Long version;

    public Ingredient() {}

    public Ingredient(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}