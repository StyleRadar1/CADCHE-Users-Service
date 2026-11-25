package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.messaging;

import edu.dosw.styleradar.usuarios.application.dto.serialization.ApiEnumSerializer;
import edu.dosw.styleradar.usuarios.domain.event.UsuarioRegistradoEvent;

record UsuarioRegistradoMessage(
        String eventId,
        String eventType,
        String occurredAt,
        Data data) {

    private static final String EVENT_TYPE =
            "styleradar.usuarios.usuario.registrado";

    static UsuarioRegistradoMessage desde(UsuarioRegistradoEvent evento) {
        return new UsuarioRegistradoMessage(
                evento.eventId(),
                EVENT_TYPE,
                evento.occurredAt().toString(),
                new Data(ApiEnumSerializer.valueOf(evento.rol())));
    }

    record Data(String rol) {
    }
}
