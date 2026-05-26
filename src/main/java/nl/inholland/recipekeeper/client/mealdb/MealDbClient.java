package nl.inholland.recipekeeper.client.mealdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.inholland.recipekeeper.exception.external.ExternalServiceException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MealDbClient {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;

    public MealDbClient(@Value("${mealdb.api.base}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public JsonNode get(String pathWithQuery) {
        String url = baseUrl + pathWithQuery;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new ExternalServiceException(
                        "MealDB request failed with status " + response.code()
                );
            }

            return objectMapper.readTree(response.body().string());

        } catch (IOException e) {
            throw new ExternalServiceException("MealDB call failed", e);
        }
    }
}