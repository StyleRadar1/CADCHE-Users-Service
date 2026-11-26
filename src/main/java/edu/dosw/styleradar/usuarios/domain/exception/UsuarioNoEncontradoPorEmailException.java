package edu.dosw.styleradar.usuarios.domain.exception;

public class UsuarioNoEncontradoPorEmailException extends RuntimeException {

    public UsuarioNoEncontradoPorEmailException() {
        super("No se encontró una cuenta con el correo indicado");
    }
}
