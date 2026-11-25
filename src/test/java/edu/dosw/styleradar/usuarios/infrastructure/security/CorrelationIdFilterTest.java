package edu.dosw.styleradar.usuarios.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void propagaXCorrelationIdYLoLimpiaDeMdcAlFinalizar() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/users/1/feed");
        request.addHeader(CorrelationIdFilter.HEADER_CORRELATION_ID, "corr-abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> mdcDurante = new AtomicReference<>();

        FilterChain chain = (req, res) -> mdcDurante.set(MDC.get(CorrelationIdFilter.MDC_KEY));
        filter.doFilter(request, response, chain);

        assertEquals("corr-abc-123", mdcDurante.get());
        assertEquals("corr-abc-123", response.getHeader(CorrelationIdFilter.HEADER_CORRELATION_ID));
        assertEquals("corr-abc-123", response.getHeader(CorrelationIdFilter.HEADER_REQUEST_ID));
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }

    @Test
    void usaXRequestIdSiNoLlegaXCorrelationIdYGeneraUuidSiFaltanAmbos() throws Exception {
        MockHttpServletRequest reqConRequestId = new MockHttpServletRequest("GET", "/api/v1/users/1/feed");
        reqConRequestId.addHeader(CorrelationIdFilter.HEADER_REQUEST_ID, "req-xyz\r\n");
        MockHttpServletResponse resConRequestId = new MockHttpServletResponse();

        filter.doFilter(reqConRequestId, resConRequestId, new MockFilterChain());
        assertEquals("req-xyz", resConRequestId.getHeader(CorrelationIdFilter.HEADER_CORRELATION_ID));

        MockHttpServletRequest reqSinCabeceras = new MockHttpServletRequest("GET", "/api/v1/users/1/feed");
        MockHttpServletResponse resSinCabeceras = new MockHttpServletResponse();

        filter.doFilter(reqSinCabeceras, resSinCabeceras, new MockFilterChain());
        String generado = resSinCabeceras.getHeader(CorrelationIdFilter.HEADER_CORRELATION_ID);
        assertNotNull(generado);
        assertFalse(generado.isBlank());
        assertNull(MDC.get(CorrelationIdFilter.MDC_KEY));
    }
}
