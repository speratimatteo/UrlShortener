package org.dkb.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for the successful response after shortening a URL.
 */
@Data
@AllArgsConstructor
public class ShortifyResponse {

    @Schema(description = "The unique short code generated for the URL.", example = "aBcD1eF")
    private String shortCode;

    @Schema(description = "The original long URL.",
            example = "http://example.com/very/long/path?id=123&data=abc")
    private String longUrl;
}
