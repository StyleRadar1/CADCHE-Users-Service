package edu.dosw.styleradar.usuarios.application;

import edu.dosw.styleradar.usuarios.application.usecase.ActualizarTallasHabitualesUseCaseImpl;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import edu.dosw.styleradar.usuarios.domain.port.out.TallaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarTallasHabitualesUseCaseTest {

    @Mock
    private TallaRepositoryPort tallaRepository;

    @InjectMocks
    private ActualizarTallasHabitualesUseCaseImpl useCase;

    private Map<TipoPrenda, Set<String>> tallas;

    @BeforeEach
    void setUp() {
        tallas = Map.of(
                TipoPrenda.SUPERIOR, Set.of("M"),
                TipoPrenda.INFERIOR, Set.of("32"),
                TipoPrenda.CALZADO, Set.of("40")
        );
    }

    @Test
    void debeActualizarLasTallasHabitualesDelUsuario() {
        when(tallaRepository.reemplazar(38L, tallas))
                .thenReturn(tallas);

        Map<TipoPrenda, Set<String>> resultado =
                useCase.actualizar(38L, tallas);

        assertEquals(tallas, resultado);
        verify(tallaRepository).reemplazar(38L, tallas);
    }
}
