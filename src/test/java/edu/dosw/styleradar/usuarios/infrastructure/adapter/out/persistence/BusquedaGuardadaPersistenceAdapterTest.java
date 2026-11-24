package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.dosw.styleradar.usuarios.domain.model.BusquedaGuardada;
import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity.BusquedaGuardadaEntity;
import edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.repository.BusquedaGuardadaJpaRepository;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BusquedaGuardadaPersistenceAdapterTest {

    @Mock
    private BusquedaGuardadaJpaRepository repository;

    private BusquedaGuardadaPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BusquedaGuardadaPersistenceAdapter(repository);
    }

    @Test
    void guardarMapeaTodosLosCamposYRetornaLaBusquedaPersistida() {
        BusquedaGuardada busqueda = busqueda(7L, 12L);
        when(repository.save(any(BusquedaGuardadaEntity.class))).thenAnswer(invocacion -> {
            BusquedaGuardadaEntity entity = invocacion.getArgument(0);
            entity.setId(7L);
            return entity;
        });

        BusquedaGuardada resultado = adapter.guardar(busqueda);

        assertEquals(busqueda, resultado);
        verify(repository).save(any(BusquedaGuardadaEntity.class));
    }

    @Test
    void buscarPorUsuarioConvierteLosResultados() {
        BusquedaGuardadaEntity entity = entidad(7L, 12L);
        when(repository.findAllByUsuarioIdOrderByFechaCreacionDesc(12L))
                .thenReturn(List.of(entity));

        assertEquals(List.of(busqueda(7L, 12L)), adapter.buscarPorUsuarioId(12L));
    }

    @Test
    void eliminarIndicaSiSeEliminoUnRegistro() {
        when(repository.deleteByIdAndUsuarioId(7L, 12L)).thenReturn(1L, 0L);

        assertTrue(adapter.eliminarPorIdYUsuarioId(7L, 12L));
        assertFalse(adapter.eliminarPorIdYUsuarioId(7L, 12L));
    }

    private BusquedaGuardada busqueda(Long id, Long usuarioId) {
        return new BusquedaGuardada(id, usuarioId, "Camisas", "oxford", TipoPrenda.SUPERIOR,
                "Blanco", Talla.M, 50000.0, 150000.0, "Marca", Estilo.FORMAL,
                4.6, -74.1, Instant.parse("2026-11-15T12:00:00Z"));
    }

    private BusquedaGuardadaEntity entidad(Long id, Long usuarioId) {
        BusquedaGuardadaEntity entity = new BusquedaGuardadaEntity();
        entity.setId(id);
        entity.setUsuarioId(usuarioId);
        entity.setNombre("Camisas");
        entity.setQuery("oxford");
        entity.setTipo(TipoPrenda.SUPERIOR);
        entity.setColor("Blanco");
        entity.setTalla(Talla.M);
        entity.setPrecioMin(50000.0);
        entity.setPrecioMax(150000.0);
        entity.setMarca("Marca");
        entity.setEstilo(Estilo.FORMAL);
        entity.setLatitud(4.6);
        entity.setLongitud(-74.1);
        entity.setFechaCreacion(Instant.parse("2026-11-15T12:00:00Z"));
        return entity;
    }
}
