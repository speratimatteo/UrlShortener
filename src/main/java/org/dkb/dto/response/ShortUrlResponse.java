package org.dkb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for the successful response after shortening a URL.
 */
@Data
@AllArgsConstructor
public class ShortUrlResponse {

    @Schema(description = "The unique short code generated for the URL.", example = "aBcD1eF")
    private String shortCode;

    @Schema(description = "The full shortened URL (BASE_URL + shortCode).",
            example = "https://yourservice.com/aBcD1eF")
    private String shortUrl;

    @Schema(description = "The original long URL.",
            example = "https://example.com/very/long/path?id=123&data=abc")
    private String longUrl;
}
