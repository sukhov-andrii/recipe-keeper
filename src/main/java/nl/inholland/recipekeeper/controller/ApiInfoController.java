package nl.inholland.recipekeeper.controller;

import nl.inholland.recipekeeper.model.dto.response.ApiInfoControllerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiInfoController {

    @GetMapping("/")
    public ApiInfoControllerResponse root() {
        return new ApiInfoControllerResponse(
                "recipe-keeper",
                "running",
                "/swagger-ui/index.html"
        );
    }
}