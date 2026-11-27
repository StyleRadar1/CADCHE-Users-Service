package edu.dosw.styleradar.usuarios.domain.model;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Pure domain utility to safely format public display names (e.g. "Laura Gómez Ruiz" -> "Laura G.").
 * Free of framework dependencies for maximal testability.
 */
public final class PublicProfileFormatter {

    private static final Pattern CONTROL_CHARS = Pattern.compile("[\\p{Cc}\\p{Cf}]+");
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    private static final Set<String> SURNAME_CONNECTORS = Set.of(
            "de", "del", "la", "las", "los", "y", "e", "da", "do", "dos", "das", "van", "von"
    );

    private PublicProfileFormatter() {
    }

    /**
     * Formats a user's full name into a safe public display name:
     * - Strips control/format characters and normalizes whitespace.
     * - First name + initial of first significant surname token followed by a period
     *   ("Laura Gómez" -> "Laura G.", "Andrés de la Vega" -> "Andrés V.").
     * - Single name ("Laura") -> "Laura".
     * - Null or blank input returns null.
     */
    public static String formatDisplayName(String fullName) {
        if (fullName == null) {
            return null;
        }

        String sanitized = CONTROL_CHARS.matcher(fullName).replaceAll(" ").trim();
        if (sanitized.isEmpty()) {
            return null;
        }

        String cleaned = MULTI_WHITESPACE.matcher(sanitized).replaceAll(" ");
        String[] parts = cleaned.split(" ");

        String firstName = capitalize(parts[0]);
        if (parts.length == 1) {
            return firstName;
        }

        String surnameToken = findSurnameToken(parts);
        if (surnameToken == null) {
            return firstName;
        }

        String initial = firstCodePoint(surnameToken).toUpperCase(Locale.ROOT);
        return firstName + " " + initial + ".";
    }

    private static String findSurnameToken(String[] parts) {
        for (int i = 1; i < parts.length; i++) {
            String token = parts[i];
            if (!token.isEmpty() && !SURNAME_CONNECTORS.contains(token.toLowerCase(Locale.ROOT))) {
                return token;
            }
        }
        return null;
    }

    private static String capitalize(String word) {
        int firstCp = word.codePointAt(0);
        int firstLen = Character.charCount(firstCp);
        String head = new String(Character.toChars(firstCp)).toUpperCase(Locale.ROOT);
        if (word.length() == firstLen) {
            return head;
        }
        return head + word.substring(firstLen).toLowerCase(Locale.ROOT);
    }

    private static String firstCodePoint(String word) {
        int cp = word.codePointAt(0);
        return new String(Character.toChars(cp));
    }
}
