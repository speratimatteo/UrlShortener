package org.dkb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dkb.dto.request.LongUrlRequest;
import org.dkb.dto.response.ShortifyResponse;
import org.dkb.service.ShortenerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * REST Controller for URL shortening and redirection operations.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "Endpoints for shortening and resolving URLs.")
public class UrlShortenerController {

    private final ShortenerService shortenerService;

    /**
     * Endpoint to shorten a long URL.
     *
     * @param request The DTO containing the long URL.
     * @return A response entity containing the short code and the full short URL.
     */
    @PostMapping("/shorten")
    @Operation(summary = "Shorten a long URL",
            description = "Generates a unique short code for the provided URL.")
    @ApiResponse(responseCode = "201", description = "URL successfully shortened.")
    @ApiResponse(responseCode = "400", description = "Invalid URL input or validation error.")
    public ResponseEntity<ShortifyResponse> shortenUrl(@Valid @RequestBody LongUrlRequest request) {

        String shortCode = shortenerService.shorten(request.getLongUrl());
        ShortifyResponse response = new ShortifyResponse(
                shortCode,
                request.getLongUrl()
        );
        log.info("RECORD CREATED: \n" + response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint to get the full URL corresponding to the short code passed as path variable.
     * This is the high-traffic, highly cached endpoint.
     *
     * @param shortCode The short code (hash) from the URL path.
     * @return the String with the full URL
     */
    @GetMapping("/getFullUrl/{shortCode:[0-9A-Za-z]+}")
    @Operation(summary = "Returns a ShortifyResponse with the original full URL",
            description = "Uses the short code to find and redirect to the original URL.")
    @ApiResponse(responseCode = "200", description = "Record found.")
    @ApiResponse(responseCode = "404", description = "Short code not found.")
    public ShortifyResponse getFullUrl(@PathVariable String shortCode) {

        String fullUrl = shortenerService.resolve(shortCode);
        ShortifyResponse response = new ShortifyResponse(
                shortCode,
                fullUrl
        );
        log.info("RECORD FOUND: \n" + response);
        return response;
    }

    /**
     * Endpoint to resolve a short code and redirect to the original long URL.
     * This is the high-traffic, highly cached endpoint.
     *
     * @param shortCode The short code (hash) from the URL path.
     * @return A RedirectView object for HTTP 301 redirection.
     */
    @GetMapping("/redirect/{shortCode:[0-9A-Za-z]+}")
    @Operation(summary = "Redirect to the original long URL",
            description = "Uses the shortCode to find and redirect to the original URL.")
    @ApiResponse(responseCode = "301", description = "Successful permanent redirection.")
    @ApiResponse(responseCode = "404", description = "Short code not found.")
    public RedirectView redirectToFullUrl(@PathVariable String shortCode) {

        String fullUrl = shortenerService.resolve(shortCode);

        // Set up a permanent redirect (301) for best SEO and client/CDN caching
        RedirectView redirectView = new RedirectView(fullUrl);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        return redirectView;
    }
}
