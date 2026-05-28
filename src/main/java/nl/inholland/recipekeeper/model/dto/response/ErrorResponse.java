package nl.inholland.recipekeeper.model.dto.response;

import nl.inholland.recipekeeper.exception.ErrorCode;

import java.time.Instant;

public record ErrorResponse(

        Instant timestamp,

        int status,

        String error,

        ErrorCode code,

        String message,

        String path

) {}