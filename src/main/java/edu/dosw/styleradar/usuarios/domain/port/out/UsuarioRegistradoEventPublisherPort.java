package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;

public interface UsuarioRegistradoEventPublisherPort {

    void publicar(UsuarioRegistradoEvent evento);
}
