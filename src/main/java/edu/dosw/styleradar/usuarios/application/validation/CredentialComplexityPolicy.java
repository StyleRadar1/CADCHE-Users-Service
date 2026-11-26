package edu.dosw.styleradar.usuarios.application.validation;

public final class CredentialComplexityPolicy {

    public static final String COMPLEXITY_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d\\s]).{8,72}$";

    private CredentialComplexityPolicy() {
    }
}
