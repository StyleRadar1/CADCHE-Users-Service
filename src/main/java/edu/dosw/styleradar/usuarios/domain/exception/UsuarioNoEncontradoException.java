package edu.dosw.styleradar.usuarios.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("No se encontró el usuario con ID: " + id);
    }
}
