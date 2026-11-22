package edu.dosw.styleradar.usuarios.domain.port.out;

import edu.dosw.styleradar.usuarios.domain.event.BusquedaSincronizadaEvent;

public interface BusquedaSincronizadaEventPublisherPort {

    void publicar(BusquedaSincronizadaEvent evento);
}
