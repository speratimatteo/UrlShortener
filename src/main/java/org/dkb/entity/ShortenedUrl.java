package org.dkb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "shortened_url", indexes = {
        @Index(name = "idx_short_code", columnList = "shortCode", unique = true) // Cruciale per la ricerca veloce
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortenedUrl {

    /**
     * Chiave primaria. L'ID generato dal DB che useremo per creare il codice breve (shortCode).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * L'URL originale fornito dall'utente.
     */
    @Column(name = "long_url", nullable = false, unique = true, length = 2048)
    private String longUrl;

    /**
     * Il codice breve univoco (es. "aBcD1eF").
     * È la parte variabile dell'URL accorciato completo (shortUrl = BASE_URL + shortCode).
     */
    @Column(name = "short_code", unique = true, length = 8)
    private String shortCode;

    /**
     * Data e ora di creazione del record.
     */
    @CreationTimestamp
    private Instant createdAt;
}