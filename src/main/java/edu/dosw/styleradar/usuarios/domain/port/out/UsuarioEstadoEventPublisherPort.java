package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioEstadoCambiadoEvent;

public interface UsuarioEstadoEventPublisherPort {

    void publicar(UsuarioEstadoCambiadoEvent evento);
}