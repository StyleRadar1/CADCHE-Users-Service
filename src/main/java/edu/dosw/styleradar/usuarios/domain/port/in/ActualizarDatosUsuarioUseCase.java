package edu.dosw.styleradar.usuarios.domain.port.in;

public interface ActualizarDatosUsuarioUseCase {

    void actualizar(Long usuarioId, String nombre, String telefono, String barrio);
}
