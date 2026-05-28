package nl.inholland.recipekeeper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recipeApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recipe Keeper API")
                        .version("1.0")
                        .description("Backend API for managing a personal recipe collection. All recipe content is entered by the user. The server uses TheMealDB only to retrieve a thumbnail image during creation based on the recipe title. If TheMealDB has no match or no image, the recipe is still stored and thumbnailPath may be null or a placeholder."));
    }
}