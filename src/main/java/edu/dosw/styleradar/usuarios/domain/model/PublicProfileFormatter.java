package edu.dosw.styleradar.usuarios.domain.model;

import java.util.Locale;

/**
 * Pure domain utility to safely format public display names (e.g. "Laura Gómez Ruiz" -> "Laura G.").
 * Free of framework dependencies for maximal testability.
 */
public final class PublicProfileFormatter {

    private PublicProfileFormatter() {
    }

    /**
     * Formats a user's full name into a safe public display name:
     * - First name + initial of first surname followed by a period ("Laura Gómez" -> "Laura G.").
     * - If single name ("Laura") -> "Laura".
     * - Handles multiple spaces, accents/tildes, compound tokens.
     * - Null or blank input returns null.
     */
    public static String formatDisplayName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return null;
        }

        String cleaned = fullName.trim().replaceAll("\\s+", " ");
        String[] parts = cleaned.split(" ");

        if (parts.length == 0 || parts[0].isBlank()) {
            return null;
        }

        String firstName = capitalize(parts[0]);

        if (parts.length == 1) {
            return firstName;
        }

        String surname = parts[1];
        if (surname.isBlank()) {
            return firstName;
        }

        String initial = surname.substring(0, 1).toUpperCase(Locale.ROOT);
        return firstName + " " + initial + ".";
    }

    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        if (word.length() == 1) {
            return word.toUpperCase(Locale.ROOT);
        }
        return word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1).toLowerCase(Locale.ROOT);
    }
}
