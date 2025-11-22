package org.dkb.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for the POST request body when shortening a URL.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LongUrlRequest {

    @NotBlank(message = "The long URL is required and cannot be empty.")
    @Size(max = 2048, message = "The URL length must not exceed 2048 characters.")
    @Schema(description = "The original URL to be shortened.",
            example = "https://example.com/very/long/path?id=123&data=abc")
    private String longUrl;
}
