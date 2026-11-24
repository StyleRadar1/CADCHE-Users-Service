package edu.dosw.styleradar.usuarios.domain.model;

import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern FORMATO_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        valor = valor.trim().toLowerCase();

        if (!FORMATO_EMAIL.matcher(valor).matches()) {
            throw new IllegalArgumentException("El formato del email es inválido");
        }
    }
}

