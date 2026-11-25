package edu.dosw.styleradar.usuarios.application.validation;

public final class PasswordPolicy {

    public static final String STRONG_PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,72}$";

    private PasswordPolicy() {
    }
}
