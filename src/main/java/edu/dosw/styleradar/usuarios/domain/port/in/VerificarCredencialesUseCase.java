package edu.dosw.styleradar.usuarios.domain.port.in;

import edu.dosw.styleradar.usuarios.domain.model.Usuario;

import java.util.Optional;

public interface VerificarCredencialesUseCase {

    Optional<Usuario> verificar(String email, String password);
}