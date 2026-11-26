package edu.dosw.styleradar.usuarios.domain.port.in;

public interface CambiarContrasenaUseCase {

    void ejecutar(Long usuarioId, String contrasenaActual, String contrasenaNueva);

    void restablecerInternamente(Long usuarioId, String contrasenaNueva);
}
