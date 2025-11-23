package org.dkb.service;

import org.springframework.stereotype.Service;

/**
 * Service to handle the encoding and decoding between a database ID (Long)
 * and a Base62 string (the short code).
 * Base62 is used for efficient and short hash generation from sequential IDs.
 */
@Service
public class Base62ConverterService {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_CHARS.length();

    /**
     * Encodes a long integer ID into a Base62 string (the short code).
     * The process is similar to converting a number from Base 10 to Base 62.
     *
     * @param id The auto-generated ID from the database (e.g., 12345).
     * @return The Base62 short code string (e.g., "33r").
     */
    public String encode(long id) {
        if (id == 0) {
            return String.valueOf(BASE62_CHARS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        long tempId = id;
        while (tempId > 0) {
            // Prepend the remainder
            sb.insert(0, BASE62_CHARS.charAt((int) (tempId % BASE)));
            tempId /= BASE;
        }
        return sb.toString();
    }
}
