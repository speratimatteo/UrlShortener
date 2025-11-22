package org.dkb.repository;

import org.dkb.entity.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {

    /**
     * Finds the full URL associated to the short one
     */
    Optional<ShortenedUrl> findByShortCode(String shortCode);

    /**
     * Finds the short URL associated to the long one
     */
    Optional<ShortenedUrl> findByLongUrl(String longUrl);
}
