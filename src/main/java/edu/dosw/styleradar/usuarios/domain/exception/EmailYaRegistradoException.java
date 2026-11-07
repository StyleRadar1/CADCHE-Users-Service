package edu.dosw.styleradar.usuarios.domain.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El correo electrónico " + email + " ya se encuentra registrado");
    }
}
