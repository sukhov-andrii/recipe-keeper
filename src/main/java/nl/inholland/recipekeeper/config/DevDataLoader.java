package nl.inholland.recipekeeper.config;

import lombok.extern.slf4j.Slf4j;
import nl.inholland.recipekeeper.repository.RecipeRepository;
import nl.inholland.recipekeeper.service.RecipeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;

    public DevDataLoader(RecipeService recipeService,
                         RecipeRepository recipeRepository) {
        this.recipeService = recipeService;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) {

        log.info("DevDataLoader started");
        log.info("Database contains {} recipes", recipeRepository.count());

        // Guard: only run if DB is empty
        if (recipeRepository.count() > 0) {
            log.info("Database already contains data. Skipping seeding.");
            return;
        }

        List<String> recipes = List.of(
                "Ramen Noodles with Boiled Egg",
                "Rappie Pie",
                "Raspberry mousse",
                "Raspeballer",
                "Ratatouille",
                "Recheado Masala Fish",
                "Red curry chicken kebabs",
                "Red onion pickle",
                "Red Peas Soup",
                "Ribollita",
                "Rice paper dumplings",
                "Rigatoni with fennel sausage sauce",
                "Roast aubergine with goat's cheese & toasted flatbread",
                "Roast fennel and aubergine paella",
                "Roasted chicken with creamy walnut sauce",
                "Roasted Eggplant With Tahini, Pine Nuts, and Lentils",
                "Rock Cakes",
                "Rocky Road Fudge",
                "Rogaliki",
                "Rømmegrøt",
                "Rosemary braised red cabbage with kabanos",
                "Rosol",
                "Roti john",
                "Rye bread"
        );

        log.info("Seeder will attempt to import {} recipes", recipes.size());

        for (String name : recipes) {
            try {
                log.info("Importing recipe: {}", name);
                recipeService.importFromMealDb(name);
            } catch (Exception e) {
                log.warn("Failed to import recipe: {}", name, e);
            }
        }

        log.info("Database contains {} recipes", recipeRepository.count());

        log.info("DevDataLoader finished");
    }
}