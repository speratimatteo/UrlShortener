package org.dkb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dkb.dto.request.LongUrlRequest;
import org.dkb.dto.response.ShortUrlResponse;
import org.dkb.service.ShortenerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

/**
 * REST Controller for URL shortening and redirection operations.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "Endpoints for shortening and resolving URLs.")
public class UrlShortenerController {

    private final ShortenerService shortenerService;

    @Value("${app.base-url}")
    private String baseUrl;

    /**
     * Endpoint to shorten a long URL.
     *
     * @param request The DTO containing the long URL.
     * @return A response entity containing the short code and the full short URL.
     */
    @PostMapping("/api/v1/shorten")
    @Operation(summary = "Shorten a long URL",
            description = "Generates a unique short code for the provided URL.")
    @ApiResponse(responseCode = "201", description = "URL successfully shortened.")
    @ApiResponse(responseCode = "400", description = "Invalid URL input or validation error.")
    public ResponseEntity<ShortUrlResponse> shortenUrl(@Valid @RequestBody LongUrlRequest request) {

        String shortCode = shortenerService.shorten(request.getLongUrl());

        ShortUrlResponse response = new ShortUrlResponse(
                shortCode,
                baseUrl + shortCode,
                request.getLongUrl()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to resolve a short code and redirect to the original long URL.
     * This is the high-traffic, highly cached endpoint.
     *
     * @param shortCode The short code (hash) from the URL path.
     * @return A RedirectView object for HTTP 301 redirection.
     */
    @GetMapping("/{shortCode:[0-9A-Za-z]+}")
    @Operation(summary = "Redirect to the original long URL",
            description = "Uses the short code to find and redirect to the original URL.")
    @ApiResponse(responseCode = "301", description = "Successful permanent redirection.")
    @ApiResponse(responseCode = "404", description = "Short code not found.")
    public RedirectView redirectToLongUrl(@PathVariable String shortCode) {

        String longUrl = shortenerService.resolve(shortCode);

        if (longUrl == null) {
            // Throw a 404 error if the code is not found in cache or DB
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found for code: " + shortCode);
        }

        // Set up a permanent redirect (301) for best SEO and client/CDN caching
        RedirectView redirectView = new RedirectView(longUrl);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
