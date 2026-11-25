package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.event.UsuarioContrasenaCambiadaEvent;

public interface UsuarioContrasenaCambiadaEventPublisherPort {

    void publicar(UsuarioContrasenaCambiadaEvent evento);
}
