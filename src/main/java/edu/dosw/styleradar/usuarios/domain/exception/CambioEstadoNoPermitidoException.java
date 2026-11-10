package edu.dosw.styleradar.usuarios.domain.exception;

public class CambioEstadoNoPermitidoException extends RuntimeException {

    public CambioEstadoNoPermitidoException(String mensaje) {
        super(mensaje);
    }
}