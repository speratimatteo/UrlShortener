package org.dkb.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.dkb.entity.ShortenedUrl;
import org.dkb.repository.ShortenedUrlRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Core business logic service for URL shortening and resolution.
 * Handles database interaction, hash generation, and caching.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortenerService {

    private final ShortenedUrlRepository repository;
    private final Base62ConverterService converterService;

    /**
     * Shortens a given long URL.
     *
     * @param longUrl The original URL to be shortened.
     * @return The generated short code.
     */
    @Transactional
    public String shorten(String longUrl) {

        try {
            new URL(longUrl); // delle volte URL permette invalidi, ma per MVP ok
        } catch (MalformedURLException e) {
            String errorMsg = "Invalid URL: " + longUrl;
            log.info(errorMsg);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
        }

        // 1. Check if the URL is already shortened (prevent duplicates)
        Optional<ShortenedUrl> existing = repository.findByLongUrl(longUrl);
        if (existing.isPresent()) {
            // Return the existing short code
            log.info("Record already existing for url " + longUrl);
            return existing.get().getShortCode();
        }

        // 2. Create a placeholder record to get the unique ID from PostgreSQL
        ShortenedUrl newUrl = new ShortenedUrl();
        newUrl.setLongUrl(longUrl);
        // Temporarily set a blank shortCode, will be updated later
        newUrl.setShortCode("");

        // Save the placeholder to get the auto-generated ID (PK)
        ShortenedUrl savedUrl = repository.save(newUrl);
        Long uniqueId = savedUrl.getId();

        // 3. Generate the short code using the unique ID and a random alphanumeric String
        String shortCode = converterService.encode(uniqueId) + RandomStringUtils.randomAlphanumeric(4);

        // 4. Update the record with the generated short code
        savedUrl.setShortCode(shortCode);

        // Note: Because of @Transactional, this update will be committed at the end
        // of the method, effectively completing the URL mapping.
        repository.save(savedUrl);

        return shortCode;
    }

    /**
     * Resolves a short code back to the original long URL.
     * This method is highly optimized using caching (Redis).
     *
     * @param shortCode The short code (e.g., "aBcD1").
     * @return The original long URL, or null if not found.
     */
    @Cacheable(value = "urls", key = "#shortCode")
    public String resolve(String shortCode) {
        // 1. Cacheable annotation handles the Redis lookup first.
        // If found in cache, it returns the value immediately without executing the method body.

        // 2. If not found in cache (cache miss), query the database.
        Optional<ShortenedUrl> found = repository.findByShortCode(shortCode);

        // 3. If found in DB, Spring automatically puts the result in cache for next time (Cache Hit).
        // Otherwise, throw a ResponseStatusException 404
        if (found.isPresent()) {
            return found.get().getLongUrl();
        } else {
            String errorMsg = "URL not found for code: " + shortCode;
            log.info(errorMsg);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found for code: " + shortCode);
        }
    }
}
