package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioCuentaEliminadaEvent;

public interface UsuarioCuentaEliminadaEventPublisherPort {

    void publicar(UsuarioCuentaEliminadaEvent evento);
}
