package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import edu.dosw.styleradar.usuarios.domain.model.Talla;
import edu.dosw.styleradar.usuarios.domain.model.TipoPrenda;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "busquedas_guardadas")
public class BusquedaGuardadaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(nullable = false, length = 100)
    private String nombre;
    @Column(name = "texto_busqueda")
    private String query;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoPrenda tipo;
    @Column(length = 80)
    private String color;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Talla talla;
    private Double precioMin;
    private Double precioMax;
    @Column(length = 100)
    private String marca;
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Estilo estilo;
    private Double latitud;
    private Double longitud;
    @Column(name = "fecha_creacion", nullable = false)
    private Instant fechaCreacion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public TipoPrenda getTipo() { return tipo; }
    public void setTipo(TipoPrenda tipo) { this.tipo = tipo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public Talla getTalla() { return talla; }
    public void setTalla(Talla talla) { this.talla = talla; }
    public Double getPrecioMin() { return precioMin; }
    public void setPrecioMin(Double precioMin) { this.precioMin = precioMin; }
    public Double getPrecioMax() { return precioMax; }
    public void setPrecioMax(Double precioMax) { this.precioMax = precioMax; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Estilo getEstilo() { return estilo; }
    public void setEstilo(Estilo estilo) { this.estilo = estilo; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Instant getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Instant fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
