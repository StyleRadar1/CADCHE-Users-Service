package edu.dosw.styleradar.usuarios.domain.exception;

public class CambioEstadoNoPermitidoException extends RuntimeException {

    private final String code;

    public CambioEstadoNoPermitidoException(String mensaje) {
        this(mensaje, "SELF_STATUS_CHANGE_NOT_ALLOWED");
    }

    public CambioEstadoNoPermitidoException(String mensaje, String code) {
        super(mensaje);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
