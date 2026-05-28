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
public class Ingredient {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String name;

    public Ingredient(String name) {
        this.name = name;
    }
}
