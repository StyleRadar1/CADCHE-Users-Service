package edu.dosw.styleradar.usuarios.domain.exception;

public class ContrasenaActualIncorrectaException extends RuntimeException {

    public ContrasenaActualIncorrectaException() {
        super("La contraseña actual no es correcta");
    }
}
