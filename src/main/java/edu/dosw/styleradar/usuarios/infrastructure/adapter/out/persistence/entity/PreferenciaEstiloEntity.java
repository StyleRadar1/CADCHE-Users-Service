package edu.dosw.styleradar.usuarios.infrastructure.adapter.out.persistence.entity;

import edu.dosw.styleradar.usuarios.domain.model.Estilo;
import jakarta.persistence.*;

@Entity
@Table(
        name = "usuario_preferencias_estilo",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_usuario_estilo",
                columnNames = {"usuario_id", "estilo"}
        )
)
public class PreferenciaEstiloEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Estilo estilo;

    public PreferenciaEstiloEntity() {
    }

    public PreferenciaEstiloEntity(UsuarioEntity usuario, Estilo estilo) {
        this.usuario = usuario;
        this.estilo = estilo;
    }

    public Long getId() {
        return id;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public Estilo getEstilo() {
        return estilo;
    }
}